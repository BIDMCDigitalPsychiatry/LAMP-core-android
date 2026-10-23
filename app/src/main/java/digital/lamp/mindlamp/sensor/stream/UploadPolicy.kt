package digital.lamp.mindlamp.sensor.stream

/** What to do with a local frame after an upload attempt. */
enum class UploadAction { DELETE, QUARANTINE, RETRY_LATER }

/**
 * Maps a sensor-stream upload response to a client action, per the contract table.
 */
object UploadPolicy {
    fun actionFor(httpCode: Int?): UploadAction = TODO("not implemented")
}
