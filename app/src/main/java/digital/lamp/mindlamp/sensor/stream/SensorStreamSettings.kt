package digital.lamp.mindlamp.sensor.stream

import android.content.Context

/**
 * Remembers whether `lamp.gps1m` is configured, so `LampForegroundService` can choose its
 * foreground-service type in `onStartCommand` without a database read on the main thread.
 */
object SensorStreamSettings {
    private const val PREFS = "sensor_stream"
    private const val KEY_GPS1M = "gps1m_configured"

    private fun prefs(context: Context) = context.applicationContext
        .createDeviceProtectedStorageContext()
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isGps1mConfigured(context: Context): Boolean = prefs(context).getBoolean(KEY_GPS1M, false)

    fun setGps1mConfigured(context: Context, configured: Boolean) {
        prefs(context).edit().putBoolean(KEY_GPS1M, configured).apply()
    }
}
