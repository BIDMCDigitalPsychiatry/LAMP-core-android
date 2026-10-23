package digital.lamp.mindlamp.sensor.stream

import okhttp3.MultipartBody
import okhttp3.Request

/**
 * Builds the `POST /participant/{participant_id}/sensor-stream` request for one frame.
 */
object MultipartFrameRequest {
    val FIELD_ORDER = listOf(
        "sensor_spec", "start_timestamp", "end_timestamp", "sample_count", "codec",
        "codec_format_version", "platform", "unit", "sha256"
    )
    const val FILE_FIELD = "frame"

    fun url(serverAddress: String, participantId: String): String = TODO("not implemented")

    fun body(meta: FrameMetadata, frame: ByteArray): MultipartBody = TODO("not implemented")

    fun build(serverAddress: String, participantId: String, authorization: String, meta: FrameMetadata, frame: ByteArray): Request = TODO("not implemented")
}
