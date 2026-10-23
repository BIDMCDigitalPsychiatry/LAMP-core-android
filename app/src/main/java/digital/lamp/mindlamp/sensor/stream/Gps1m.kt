package digital.lamp.mindlamp.sensor.stream

import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * The `lamp.gps1m` location request: one high-accuracy fix per 60 s.
 *
 * Uses the play-services-location 18.x API (`LocationRequest.create()`), the version
 * LAMP-kotlin's `lamp.gps` needs at runtime.
 */
object Gps1mRequest {
    const val SPEC = "lamp.gps1m"
    const val INTERVAL_MS = 60_000L
    const val FASTEST_INTERVAL_MS = 60_000L

    val priority: Int get() = LocationRequest.PRIORITY_HIGH_ACCURACY

    fun create(): LocationRequest = LocationRequest.create()
        .setInterval(INTERVAL_MS)
        .setFastestInterval(FASTEST_INTERVAL_MS)
        .setPriority(priority)
}

/**
 * Builds `lamp.gps1m` sensor events with the `lamp.gps` data shape.
 */
object Gps1mEvents {
    private val gson = Gson()

    /** Returns one `sensor_event` JSON object. Missing altitude or accuracy is omitted. */
    fun toJson(timestamp: Long, latitude: Double, longitude: Double, altitude: Double?, accuracy: Float?): String {
        val data = JsonObject().apply {
            addProperty("latitude", latitude)
            addProperty("longitude", longitude)
            altitude?.let { addProperty("altitude", it) }
            accuracy?.let { addProperty("accuracy", it.toDouble()) }
        }
        val event = JsonObject().apply {
            addProperty("timestamp", timestamp)
            addProperty("sensor", Gps1mRequest.SPEC)
            add("data", data)
        }
        return gson.toJson(event)
    }

    /** Wraps events in the JSON array that `POST .../sensor_event` accepts. */
    fun toJsonArray(events: List<String>): String {
        val array = JsonArray()
        events.forEach { array.add(JsonParser().parse(it)) }
        return gson.toJson(array)
    }
}

/**
 * A small file queue of `lamp.gps1m` events, one JSON file per fix, oldest first.
 */
class Gps1mQueue(val dir: File) {
    class Batch(val files: List<File>, val events: List<String>)

    private var lastName = 0L

    @Synchronized
    fun append(eventJson: String) {
        dir.mkdirs()
        // Names sort in append order, even for two fixes in the same millisecond.
        lastName = maxOf(lastName + 1, System.currentTimeMillis() * 1000)
        val target = File(dir, "%020d.json".format(lastName))
        val tmp = File(dir, target.name + ".tmp")
        FileOutputStream(tmp).use { it.write(eventJson.toByteArray(Charsets.UTF_8)) }
        if (!tmp.renameTo(target)) {
            tmp.delete()
            throw IOException("could not write $target")
        }
    }

    /**
     * Appends each of [eventsJson] and returns how many were written. A failed write (for
     * example, a full disk) is skipped instead of thrown, so one bad fix can't stop the rest.
     */
    fun appendAll(eventsJson: List<String>): Int = eventsJson.count { event ->
        try {
            append(event)
            true
        } catch (e: IOException) {
            false
        }
    }

    fun size(): Int = files().size

    /** Returns up to [limit] of the oldest events without removing them. */
    fun next(limit: Int): Batch {
        val files = files().take(limit)
        return Batch(files, files.map { it.readText(Charsets.UTF_8) })
    }

    /** Removes an uploaded batch. */
    fun remove(batch: Batch) {
        batch.files.forEach { it.delete() }
    }

    private fun files(): List<File> =
        dir.listFiles { f -> f.name.endsWith(".json") }?.sortedBy { it.name } ?: emptyList()
}
