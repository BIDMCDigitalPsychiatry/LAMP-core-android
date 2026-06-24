package digital.lamp.mindlamp.auth

import digital.lamp.mindlamp.appstate.AppState
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
 */
class TokenAuthenticator : Authenticator {

    // Dedicated client with NO authenticator, so the refresh call can't recurse.
    private val refreshClient = OkHttpClient()
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Only session/Bearer users refresh. Basic / external JWT: don't retry.
        if (AppState.session.refreshToken.isEmpty()) return null

        // Stop runaway loops (e.g. server keeps 401ing after a "successful" refresh).
        if (responseCount(response) >= 2) return null

        synchronized(lock) {
            val currentAccess = AppState.session.accessToken
            val failedAuth = response.request.header("Authorization")

            // Another thread may have already refreshed while this request was
            // in flight; if so, just retry with the current token.
            if (failedAuth != null && failedAuth != "Bearer $currentAccess") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccess")
                    .build()
            }

            val newAccess = refreshTokens()
            if (newAccess == null) {
                // Refresh token is dead -> the session can't be renewed.
                // Clear it so further 401s don't loop, and signal re-login.
                AppState.session.clearData()
                SessionExpiredNotifier.notifyExpired()
                return null
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
        }
    }

    /**
     * POST {server}/mobile-token/refresh {refreshToken} -> {accessToken,
     * refreshToken} (flat camelCase). The refresh token ROTATES and is
     * single-use, so the new one MUST be persisted or the next refresh 400s.
     * Returns the new access token, or null if refresh failed (dead session).
     */
    private fun refreshTokens(): String? {
        return try {
            val server = AppState.session.serverAddress.trimEnd('/')
            val payload = JSONObject().put("refreshToken", AppState.session.refreshToken)
                .toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$server/mobile-token/refresh")
                .post(payload)
                .build()
            refreshClient.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return null   // 400 invalid-refresh-token -> dead
                val json = JSONObject(resp.body?.string() ?: return null)
                val access = json.optString("accessToken", "")
                val refresh = json.optString("refreshToken", "")
                if (access.isEmpty() || refresh.isEmpty()) return null
                AppState.session.accessToken = access
                AppState.session.refreshToken = refresh   // persist the rotated token
                access
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var prior = response.priorResponse
        var count = 1
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
