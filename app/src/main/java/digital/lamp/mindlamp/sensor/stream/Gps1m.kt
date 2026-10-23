package digital.lamp.mindlamp.sensor.stream

import com.google.android.gms.location.LocationRequest
import java.io.File

/**
 * The `lamp.gps1m` location request: one high-accuracy fix per 60 s.
 */
object Gps1mRequest {
    const val SPEC = "lamp.gps1m"
    const val INTERVAL_MS = 60_000L
    const val FASTEST_INTERVAL_MS = 60_000L

    val priority: Int get() = TODO("not implemented")

    fun create(): LocationRequest = TODO("not implemented")
}

/**
 * Builds `lamp.gps1m` sensor events with the `lamp.gps` data shape.
 */
object Gps1mEvents {
    fun toJson(timestamp: Long, latitude: Double, longitude: Double, altitude: Double?, accuracy: Float?): String = TODO("not implemented")

    fun toJsonArray(events: List<String>): String = TODO("not implemented")
}

/**
 * A small file queue of `lamp.gps1m` events, one JSON file per fix.
 */
class Gps1mQueue(val dir: File) {
    class Batch(val files: List<File>, val events: List<String>)

    fun append(eventJson: String): Unit = TODO("not implemented")

    fun size(): Int = TODO("not implemented")

    fun next(limit: Int): Batch = TODO("not implemented")

    fun remove(batch: Batch): Unit = TODO("not implemented")
}
