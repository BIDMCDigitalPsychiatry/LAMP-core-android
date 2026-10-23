package digital.lamp.mindlamp.sensor.stream

/**
 * The multipart text fields of one sealed frame, in contract order.
 */
data class FrameMetadata(
    val sensorSpec: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val sampleCount: Int,
    val codec: String,
    val codecFormatVersion: Int,
    val sha256: String,
    val byteLength: Int,
    val platform: String = PLATFORM,
    val unit: String = UNIT
) {
    fun formFields(): List<Pair<String, String>> = TODO("not implemented")

    companion object {
        const val PLATFORM = "android"
        const val UNIT = "m/s^2"

        fun forFrame(sensorSpec: String, sortedRecords: List<AccelRecord>, compressed: ByteArray, compressor: FrameCompressor): FrameMetadata = TODO("not implemented")
    }
}

object Sha256 {
    fun hex(bytes: ByteArray): String = TODO("not implemented")
}
