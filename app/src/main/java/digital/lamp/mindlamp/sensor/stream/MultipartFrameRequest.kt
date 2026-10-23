package digital.lamp.mindlamp.sensor.stream

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Builds the `POST /participant/{participant_id}/sensor-stream` request for one frame.
 */
object MultipartFrameRequest {
    /** The text fields in the order the server requires. */
    val FIELD_ORDER = listOf(
        "sensor_spec", "start_timestamp", "end_timestamp", "sample_count", "codec",
        "codec_format_version", "platform", "unit", "sha256"
    )
    const val FILE_FIELD = "frame"
    private val OCTET_STREAM = "application/octet-stream".toMediaType()

    /** Returns the upload URL. A server address without a scheme gets `https://`. */
    fun url(serverAddress: String, participantId: String): String =
        "${participantUrl(serverAddress, participantId)}/sensor-stream"

    /** Returns `<server>/participant/<participantId>`, adding `https://` when there's no scheme. */
    fun participantUrl(serverAddress: String, participantId: String): String {
        val base = serverAddress.trim().trimEnd('/')
        val withScheme = if (base.startsWith("http://") || base.startsWith("https://")) base else "https://$base"
        return "$withScheme/participant/$participantId"
    }

    /** Returns the multipart body: every text field first, then the `frame` file part. */
    fun body(meta: FrameMetadata, frame: ByteArray): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        meta.formFields().forEach { (name, value) -> builder.addFormDataPart(name, value) }
        builder.addFormDataPart(FILE_FIELD, "frame.${meta.codec}", frame.toRequestBody(OCTET_STREAM))
        return builder.build()
    }

    fun build(
        serverAddress: String,
        participantId: String,
        authorization: String,
        meta: FrameMetadata,
        frame: ByteArray
    ): Request = Request.Builder()
        .url(url(serverAddress, participantId))
        .header("Authorization", authorization)
        .post(body(meta, frame))
        .build()
}
