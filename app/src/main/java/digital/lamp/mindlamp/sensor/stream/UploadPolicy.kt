package digital.lamp.mindlamp.sensor.stream

/** What to do with a local frame after an upload attempt. */
enum class UploadAction { DELETE, QUARANTINE, RETRY_LATER }

/**
 * Maps a sensor-stream upload response to a client action, per the contract table.
 */
object UploadPolicy {
    /**
     * Returns the action for [httpCode], or for a network error when [httpCode] is `null`.
     * A `200` duplicate is deleted like any other success. `400`, `409` and `413` are never
     * retried. Everything else, including `401`, `403` and `5xx`, keeps the frame for a retry.
     */
    fun actionFor(httpCode: Int?): UploadAction = when {
        httpCode == null -> UploadAction.RETRY_LATER
        httpCode in 200..299 -> UploadAction.DELETE
        httpCode == 400 || httpCode == 409 || httpCode == 413 -> UploadAction.QUARANTINE
        else -> UploadAction.RETRY_LATER
    }
}
