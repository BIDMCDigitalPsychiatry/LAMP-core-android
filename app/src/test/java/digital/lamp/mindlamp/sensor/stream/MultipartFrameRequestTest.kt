package digital.lamp.mindlamp.sensor.stream

import okio.Buffer
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MultipartFrameRequestTest {

    private val meta = Vectors.meta()["frame.openzl"].asJsonObject.let {
        FrameMetadata(
            sensorSpec = it["sensor_spec"].asString,
            startTimestamp = it["start_timestamp"].asLong,
            endTimestamp = it["end_timestamp"].asLong,
            sampleCount = it["sample_count"].asInt,
            codec = it["codec"].asString,
            codecFormatVersion = it["codec_format_version"].asInt,
            sha256 = it["sha256"].asString,
            byteLength = it["byte_length"].asInt
        )
    }
    private val frame = Vectors.bytes("frame.openzl")

    @Test
    fun formFieldsFollowContractOrder() {
        assertEquals(MultipartFrameRequest.FIELD_ORDER, meta.formFields().map { it.first })
        assertEquals(
            listOf("sensor_spec", "start_timestamp", "end_timestamp", "sample_count", "codec",
                "codec_format_version", "platform", "unit", "sha256"),
            MultipartFrameRequest.FIELD_ORDER
        )
    }

    @Test
    fun bodyHasTextFieldsFirstAndFramePartLast() {
        val body = MultipartFrameRequest.body(meta, frame)
        assertEquals("multipart/form-data", "${body.type.type}/${body.type.subtype}")
        val names = body.parts.map { part ->
            val disposition = part.headers!!["Content-Disposition"]!!
            Regex("name=\"([^\"]+)\"").find(disposition)!!.groupValues[1]
        }
        assertEquals(MultipartFrameRequest.FIELD_ORDER + "frame", names)

        val filePart = body.parts.last()
        assertTrue(filePart.headers!!["Content-Disposition"]!!.contains("filename="))
        assertEquals("application/octet-stream", filePart.body.contentType().toString())
        val fileBytes = Buffer().also { filePart.body.writeTo(it) }.readByteArray()
        assertArrayEquals(frame, fileBytes)

        val values = body.parts.dropLast(1).map { Buffer().also { b -> it.body.writeTo(b) }.readUtf8() }
        assertEquals(meta.formFields().map { it.second }, values)
        assertEquals(Sha256.hex(frame), values.last())
    }

    @Test
    fun serializedBodyCarriesExactFrameBytesAfterAllFields() {
        val buffer = Buffer()
        MultipartFrameRequest.body(meta, frame).writeTo(buffer)
        val text = buffer.clone().readString(Charsets.ISO_8859_1)
        val positions = (MultipartFrameRequest.FIELD_ORDER + "frame").map { text.indexOf("name=\"$it\"") }
        assertTrue(positions.all { it >= 0 })
        assertEquals(positions.sorted(), positions)
        assertTrue(text.contains("a8ee18c33e9953a183864eea00deacfc3bfa6a300470a05741f6ece8003c15b6"))
    }

    @Test
    fun requestTargetsSensorStreamRouteWithAuthorization() {
        val request = MultipartFrameRequest.build("https://lamp.example.org/", "U1234567890", "Basic abc", meta, frame)
        assertEquals("POST", request.method)
        assertEquals("https://lamp.example.org/participant/U1234567890/sensor-stream", request.url.toString())
        assertEquals("Basic abc", request.header("Authorization"))
        assertEquals("https://lamp.example.org/participant/U1234567890/sensor-stream",
            MultipartFrameRequest.url("lamp.example.org", "U1234567890"))
        assertEquals("http://localhost:3000/participant/U1/sensor-stream",
            MultipartFrameRequest.url("http://localhost:3000", "U1"))
    }
}
