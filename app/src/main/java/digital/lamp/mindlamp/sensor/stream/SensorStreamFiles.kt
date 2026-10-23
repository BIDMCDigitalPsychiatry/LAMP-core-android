package digital.lamp.mindlamp.sensor.stream

import java.io.File
import java.net.URLEncoder

/**
 * The on-disk layout of sensor-stream data, scoped by participant so that data is only ever
 * uploaded for the participant who collected it.
 *
 * - `filesDir/sensor_stream/<owner>/` holds one participant's [FrameStore].
 * - `filesDir/gps1m/<owner>/` holds one participant's [Gps1mQueue].
 */
object SensorStreamFiles {
    const val SENSOR_STREAM_DIR = "sensor_stream"
    const val GPS1M_DIR = "gps1m"
    private const val OWNER_PREFIX = "p_"

    /** Returns a directory name for [participantId] that is unique and has no path separators. */
    fun ownerDirName(participantId: String): String =
        OWNER_PREFIX + URLEncoder.encode(participantId, "UTF-8")

    /** Returns the frame store of [participantId]. */
    fun frameStore(filesDir: File, participantId: String): FrameStore =
        FrameStore(File(File(filesDir, SENSOR_STREAM_DIR), ownerDirName(participantId)))

    /** Returns the frame stores of every participant with data on this device. */
    fun frameStores(filesDir: File): List<FrameStore> =
        File(filesDir, SENSOR_STREAM_DIR)
            .listFiles { f -> f.isDirectory && f.name.startsWith(OWNER_PREFIX) }
            ?.sortedBy { it.name }
            ?.map { FrameStore(it) }
            ?: emptyList()

    /** Returns the `lamp.gps1m` queue of [participantId]. */
    fun gps1mQueue(filesDir: File, participantId: String): Gps1mQueue =
        Gps1mQueue(File(File(filesDir, GPS1M_DIR), ownerDirName(participantId)))

    /** Deletes every participant's frames and `lamp.gps1m` events. Call it on logout. */
    fun clearAll(filesDir: File) {
        File(filesDir, SENSOR_STREAM_DIR).deleteRecursively()
        File(filesDir, GPS1M_DIR).deleteRecursively()
    }
}
