package digital.lamp.mindlamp.sensor.stream

import java.security.MessageDigest

/**
 * The multipart text fields of one sealed frame, plus its compressed length.
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
    /** Returns the text fields in contract order; send them before the `frame` part. */
    fun formFields(): List<Pair<String, String>> = listOf(
        "sensor_spec" to sensorSpec,
        "start_timestamp" to startTimestamp.toString(),
        "end_timestamp" to endTimestamp.toString(),
        "sample_count" to sampleCount.toString(),
        "codec" to codec,
        "codec_format_version" to codecFormatVersion.toString(),
        "platform" to platform,
        "unit" to unit,
        "sha256" to sha256
    )

    companion object {
        const val PLATFORM = "android"
        const val UNIT = "m/s^2"

        /** Describes [compressed], the output of [compressor] for [sortedRecords]. */
        fun forFrame(
            sensorSpec: String,
            sortedRecords: List<AccelRecord>,
            compressed: ByteArray,
            compressor: FrameCompressor
        ): FrameMetadata {
            require(sortedRecords.isNotEmpty()) { "a frame holds at least one record" }
            return FrameMetadata(
                sensorSpec = sensorSpec,
                startTimestamp = sortedRecords.first().timestamp,
                endTimestamp = sortedRecords.last().timestamp,
                sampleCount = sortedRecords.size,
                codec = compressor.codec,
                codecFormatVersion = compressor.formatVersion,
                sha256 = Sha256.hex(compressed),
                byteLength = compressed.size
            )
        }
    }
}

object Sha256 {
    /** Returns the lowercase hex SHA-256 digest of [bytes]. */
    fun hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
}
