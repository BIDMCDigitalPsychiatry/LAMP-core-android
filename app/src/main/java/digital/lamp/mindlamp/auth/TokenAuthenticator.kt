package digital.lamp.mindlamp.auth

import android.content.Intent
import digital.lamp.mindlamp.app.App
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.repository.LampForegroundService
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

/**
 * Refreshes the Bearer access token on a 401 and retries the request, for the
 * new session server. Registered on `ApiClient.builder` at app startup, so it
 * covers every native API call through one chokepoint.
 *
 * Bearer-gated: Basic and external-JWT users have no refresh token, so their
 * 401s pass straight through (returning null = "give up") and their session is
 * never touched. The access token is short-lived (15 min) so this is the normal
 * path for backgrounded passive collection, not an edge case.
 *
 * A failed refresh only ends the session when the server REJECTS the refresh
 * token (400/401). Transient failures (offline, 5xx during a deploy, parse
 * errors) fail the current request but leave the session intact — the refresh
 * token wasn't consumed, so the next 401 simply retries.
 */
class TokenAuthenticator : Authenticator {

    private sealed class RefreshResult {
        class Success(val accessToken: String) : RefreshResult()
        object Dead : RefreshResult()       // server rejected the refresh token
        object Transient : RefreshResult()  // offline / 5xx / parse — retry later
    }

    // Dedicated client with NO authenticator, so the refresh call can't recurse.
    private val refreshClient = OkHttpClient()
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Only session/Bearer users refresh. Basic / external JWT: don't retry.
        if (AppState.session.refreshToken.isEmpty()) return null

        // Stop runaway loops (e.g. server keeps 401ing after a "successful"
        // refresh). Counts only 401 rounds — see priorAuthAttempts.
        if (priorAuthAttempts(response) >= 2) return null

        synchronized(lock) {
            // Re-check under the lock: another thread's failed refresh may have
            // cleared the session while this one waited.
            if (AppState.session.refreshToken.isEmpty()) return null

            val currentAccess = AppState.session.accessToken
            val failedAuth = response.request.header("Authorization")

            // Another thread may have already refreshed while this request was
            // in flight; if so, just retry with the current token.
            if (failedAuth != null && failedAuth != "Bearer $currentAccess") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccess")
                    .build()
            }

            return when (val result = refreshTokens()) {
                is RefreshResult.Success -> response.request.newBuilder()
                    .header("Authorization", "Bearer ${result.accessToken}")
                    .build()
                RefreshResult.Dead -> {
                    // The refresh token was rejected -> the session cannot be
                    // renewed. Clear it, stop collection, and signal re-login.
                    AppState.session.clearData()
                    stopBackgroundCollection()
                    SessionExpiredNotifier.notifyExpired()
                    null
                }
                // Fail this request but keep the session; next 401 retries.
                RefreshResult.Transient -> null
            }
        }
    }

    /**
     * POST {server}/mobile-token/refresh {refreshToken} -> {accessToken,
     * refreshToken} (flat camelCase). The refresh token ROTATES and is
     * single-use, so the new one MUST be persisted or the next refresh 400s.
     */
    private fun refreshTokens(): RefreshResult {
        return try {
            val server = AppState.session.serverAddress.trimEnd('/')
            val payload = JSONObject().put("refreshToken", AppState.session.refreshToken)
                .toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$server/mobile-token/refresh")
                .post(payload)
                .build()
            refreshClient.newCall(request).execute().use { resp ->
                when {
                    // Server explicitly rejected the refresh token -> dead.
                    resp.code == 400 || resp.code == 401 -> RefreshResult.Dead
                    // 5xx / anything else unsuccessful -> transient (deploy blip).
                    !resp.isSuccessful -> RefreshResult.Transient
                    else -> {
                        val body = resp.body?.string() ?: return RefreshResult.Transient
                        val json = JSONObject(body)
                        val access = json.optString("accessToken", "")
                        val refresh = json.optString("refreshToken", "")
                        if (access.isEmpty() || refresh.isEmpty()) return RefreshResult.Transient
                        // Persist the ROTATED refresh token first: a crash
                        // between the two commits then leaves a stale access
                        // token (recoverable via the next 401->refresh) rather
                        // than a consumed refresh token (stranded session).
                        AppState.session.refreshToken = refresh
                        AppState.session.accessToken = access
                        RefreshResult.Success(access)
                    }
                }
            }
        } catch (e: Exception) {
            // IOException (offline), JSONException, bad URL, ... -> transient.
            RefreshResult.Transient
        }
    }

    /**
     * Count only prior 401 rounds. `priorResponse` also chains OkHttp's
     * redirect follows, which must not eat the retry budget — a single
     * http->https redirect would otherwise disable refresh entirely.
     */
    private fun priorAuthAttempts(response: Response): Int {
        var prior = response.priorResponse
        var count = 1
        while (prior != null) {
            if (prior.code == 401) count++
            prior = prior.priorResponse
        }
        return count
    }

    /**
     * HomeActivity's observer may not be alive when the session dies (this runs
     * on an OkHttp thread from a background worker); stop the foreground
     * service here so collection doesn't keep making doomed calls. The
     * HomeActivity observer still handles UI + alarms when it next runs
     * (LiveData retains the expired flag).
     */
    private fun stopBackgroundCollection() {
        try {
            val ctx = App.app.applicationContext
            ctx.stopService(Intent(ctx, LampForegroundService::class.java))
        } catch (_: Exception) {
        }
    }
}
