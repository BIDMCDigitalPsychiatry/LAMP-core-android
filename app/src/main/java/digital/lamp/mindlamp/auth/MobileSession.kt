package digital.lamp.mindlamp.auth

import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ApiClient
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Session-server auth for the native app: upgrades a stored Basic login to
 * mobile tokens, and builds the dashboard URL for the current login. Both share
 * one lock so a launch-time upgrade finishes before the URL is built.
 */
object MobileSession {

    private const val UPGRADE_RETRY_INTERVAL_MS = 60 * 60 * 1000L
    private val JSON = "application/json".toMediaType()

    // For the unauthenticated /server-info and /login calls
    private val plainClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val lock = Mutex()
    private var lastUpgradeAttempt = 0L

    private fun serverBase() = AppState.session.serverAddress.trimEnd('/')
    private fun serverHost() = serverBase().removePrefix("https://").removePrefix("http://")
    private fun enc(value: String) = URLEncoder.encode(value, "UTF-8")

    /**
     * If a Basic user's server now advertises session auth, swap the stored
     * password for mobile tokens via POST /login. Runs on launch and foreground
     * rather than on failure, since the server's legacy flag keeps Basic
     * requests succeeding. Any failure leaves the Basic login untouched.
     */
    suspend fun upgradeToBearerIfNeeded() {
        lock.withLock {
            val session = AppState.session
            if (!session.isLoggedIn || session.authScheme == "bearer" || session.token.isEmpty()) return
            val now = System.currentTimeMillis()
            if (now - lastUpgradeAttempt < UPGRADE_RETRY_INTERVAL_MS) return
            lastUpgradeAttempt = now

            val separator = session.token.indexOf(':')
            if (separator < 0) return
            val upgraded = withContext(Dispatchers.IO) {
                try {
                    if (!serverUsesSessionAuth()) return@withContext false
                    val body = JSONObject()
                        .put("accessKey", session.token.substring(0, separator))
                        .put("secretKey", session.token.substring(separator + 1))
                        .toString().toRequestBody(JSON)
                    val request = Request.Builder().url("${serverBase()}/login").post(body).build()
                    plainClient.newCall(request).execute().use { resp ->
                        if (!resp.isSuccessful) return@withContext false
                        val json = JSONObject(resp.body?.string() ?: return@withContext false)
                        persistTokens(json.optJSONObject("mobileAuth") ?: return@withContext false)
                    }
                } catch (e: Exception) {
                    false
                }
            }
            if (upgraded) {
                session.authScheme = "bearer"
                session.token = ""
            }
        }
    }

    /**
     * Dashboard URL for [path] ("/" for home). Basic users get the ?a= login.
     * Bearer users get a fresh single-use login token on every open; if none
     * can be fetched (offline), the plain URL falls back to the web view cookie.
     */
    suspend fun dashboardUrl(path: String = "/"): String {
        lock.withLock {
            val web = BuildConfig.BASE_URL_WEB // "https://<dashboard>/#/"
            if (AppState.session.authScheme != "bearer") {
                return web.dropLast(1) + path + "?a=" + Utils.toBase64(AppState.session.token + ":" + serverHost())
            }
            val token = withContext(Dispatchers.IO) { oneTimeLoginToken() }
                ?: return web.dropLast(1) + path
            // The dashboard reads the token from the query string, before the hash
            return web.substringBefore("#") + "?finishLoginToken=" + enc(token) +
                "&serverAddress=" + enc(serverHost()) + "#" + path
        }
    }

    private fun serverUsesSessionAuth(): Boolean {
        val request = Request.Builder().url("${serverBase()}/server-info").build()
        plainClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) return false
            val body = resp.body?.string() ?: return false
            return JSONObject(body).optString("authScheme") == "session"
        }
    }

    // Sent through the shared client so an expired access token is refreshed.
    // mobileKeys is present when the server rotated the session and must be saved.
    private fun oneTimeLoginToken(): String? {
        return try {
            val request = Request.Builder()
                .url("${serverBase()}/mobile-token/one-time-login")
                .header("Authorization", Utils.authHeader())
                .build()
            ApiClient.client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val json = JSONObject(resp.body?.string() ?: return null)
                json.optJSONObject("mobileKeys")?.let { persistTokens(it) }
                json.optString("webViewRefreshToken").ifEmpty { null }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Refresh token first, as in TokenAuthenticator
    private fun persistTokens(tokens: JSONObject): Boolean {
        val access = tokens.optString("accessToken")
        val refresh = tokens.optString("refreshToken")
        if (access.isEmpty() || refresh.isEmpty()) return false
        AppState.session.refreshToken = refresh
        AppState.session.accessToken = access
        return true
    }
}
