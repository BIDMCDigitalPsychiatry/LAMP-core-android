package digital.lamp.mindlamp.sensor.stream

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.HandlerThread
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import digital.lamp.mindlamp.utils.LampLog

/**
 * Requests one high-accuracy fix per minute for `lamp.gps1m` and queues each fix as a
 * `sensor_event`. It runs beside LAMP-kotlin's `lamp.gps` and isn't stopped by `Lamp.stopLAMP`.
 */
object Gps1mCollector {
    private const val TAG = "Gps1m"
    private val lock = Any()
    private var client: FusedLocationProviderClient? = null
    private var thread: HandlerThread? = null

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val context = appContext ?: return
            val participantId = owner ?: return
            // An exception here would escape the HandlerThread and kill the process.
            try {
                val events = result.locations.map { location ->
                    Gps1mEvents.toJson(
                        location.time,
                        location.latitude,
                        location.longitude,
                        if (location.hasAltitude()) location.altitude else null,
                        if (location.hasAccuracy()) location.accuracy else null
                    )
                }
                val written = queue(context, participantId).appendAll(events)
                if (written < events.size) LampLog.e(TAG, "lamp.gps1m queued $written of ${events.size} fixes")
                Gps1mUploadWorker.enqueue(context)
            } catch (e: Exception) {
                LampLog.e(TAG, "lamp.gps1m fix not queued: ${e.message}")
            }
        }
    }

    @Volatile
    private var appContext: Context? = null

    /** The participant whose queue receives new fixes. */
    @Volatile
    private var owner: String? = null

    val isRunning: Boolean get() = synchronized(lock) { client != null }

    /** Returns the `lamp.gps1m` queue of [participantId]. */
    fun queue(context: Context, participantId: String) =
        SensorStreamFiles.gps1mQueue(context.applicationContext.filesDir, participantId)

    fun hasFineLocation(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * Starts location updates for [participantId]. If already running, new fixes go to
     * [participantId]'s queue. Does nothing without `ACCESS_FINE_LOCATION`.
     */
    @SuppressLint("MissingPermission")
    fun start(context: Context, participantId: String) {
        synchronized(lock) {
            owner = participantId
            if (client != null) return
            if (!hasFineLocation(context)) {
                LampLog.e(TAG, "lamp.gps1m needs ACCESS_FINE_LOCATION")
                return
            }
            appContext = context.applicationContext
            val handlerThread = HandlerThread("lamp-gps1m").apply { start() }
            val fused = LocationServices.getFusedLocationProviderClient(context.applicationContext)
            try {
                fused.requestLocationUpdates(Gps1mRequest.create(), callback, handlerThread.looper)
            } catch (e: Exception) {
                LampLog.e(TAG, "requestLocationUpdates failed: ${e.message}")
                handlerThread.quitSafely()
                return
            }
            thread = handlerThread
            client = fused
        }
    }

    fun stop() {
        synchronized(lock) {
            client?.removeLocationUpdates(callback)
            thread?.quitSafely()
            client = null
            thread = null
            owner = null
        }
    }
}
