package digital.lamp.mindlamp.sensor.stream

import android.content.Context
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Shared upload plumbing for the sensor-stream and `lamp.gps1m` workers. It uses the same
 * `Authorization` header and Wi-Fi, cellular and battery rules as
 * `LampForegroundService.invokeAddSensorData`.
 */
object SensorStreamUploads {
    /** Battery percentage at or below which uploads wait, as for `sensor_event` uploads. */
    private const val MIN_BATTERY_PERCENT = 15

    val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    /** Returns `true` when a participant is signed in. */
    fun isSignedIn(): Boolean =
        AppState.session.userId.isNotEmpty() && AppState.session.token.isNotEmpty() &&
            AppState.session.serverAddress.isNotEmpty()

    /** Applies the existing network and battery gates. */
    fun canUpload(context: Context): Boolean {
        if (!AppState.session.isCellularUploadAllowed && !NetworkUtils.isWifiNetworkAvailable(context)) return false
        if (!NetworkUtils.isNetworkAvailable(context)) return false
        return NetworkUtils.getBatteryPercentage(context) > MIN_BATTERY_PERCENT
    }

    /** Builds the legacy Basic header that existing `sensor_event` uploads send. */
    fun authorizationHeader(): String = "Basic ${
        Utils.toBase64(
            AppState.session.token + ":" + AppState.session.serverAddress.removePrefix("https://")
                .removePrefix("http://")
        )
    }"
}
