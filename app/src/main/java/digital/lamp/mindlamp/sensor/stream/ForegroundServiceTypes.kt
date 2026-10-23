package digital.lamp.mindlamp.sensor.stream

import android.content.pm.ServiceInfo

/**
 * Chooses the `startForeground` service type for `LampForegroundService`.
 *
 * `health` is always set. `location` is added only when `lamp.gps1m` is configured and the
 * participant granted `ACCESS_FINE_LOCATION`; on Android 14 and later, starting a `location`
 * foreground service without that permission throws `SecurityException`.
 */
object ForegroundServiceTypes {
    fun select(gps1mConfigured: Boolean, fineLocationGranted: Boolean): Int {
        var type = ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        if (gps1mConfigured && fineLocationGranted) type = type or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        return type
    }
}
