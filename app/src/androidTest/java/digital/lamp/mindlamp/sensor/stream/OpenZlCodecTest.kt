package digital.lamp.mindlamp.sensor.stream

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okio.Buffer
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Runs the sensor-stream codecs on a device or emulator, where `libsensorstream.so` loads.
 */
@RunWith(AndroidJUnit4::class)
class OpenZlCodecTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private fun asset(name: String): ByteArray =
        instrumentation.context.assets.open("sensor-stream/$name").use { it.readBytes() }

    private fun goldenRecords(): List<AccelRecord> =
        JsonParser().parse(String(asset("records.json"))).asJsonArray.map {
            val o = it.asJsonObject
            AccelRecord(o["timestamp"].asLong, o["x"].asDouble.toFloat(), o["y"].asDouble.toFloat(), o["z"].asDouble.toFloat())
        }

    private fun is64Bit() = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty() &&
        Build.SUPPORTED_ABIS.first() in setOf("arm64-v8a", "x86_64")

    @Test
    fun nativeLibraryReportsFormatVersion24() {
        assumeTrue(is64Bit())
        assertTrue(OpenZlNative.isLibraryLoaded)
        assertTrue(OpenZlNative.isUsable())
        assertEquals(24, OpenZlNative.formatVersion())
        assertEquals("openzl", CodecSelector.forDevice().codec)
    }

    @Test
    fun decodesGoldenOpenZlFrameToFrameBin() {
        assumeTrue(is64Bit())
        val codec = OpenZlCompressor()
        assertArrayEquals(asset("frame.bin"), codec.decompress(asset("frame.openzl")))
        assertArrayEquals(asset("frame_single.bin"), codec.decompress(asset("frame_single.openzl")))
        assertEquals(24, FrameCodecs.openZlFrameFormatVersion(asset("frame.openzl")))
    }

    @Test
    fun roundTripsPackedRecords() {
        assumeTrue(is64Bit())
        val codec = OpenZlCompressor()
        val raw = AccelRecordPacker.pack(goldenRecords())
        val compressed = codec.compress(raw)
        assertTrue("compressed ${compressed.size} >= ${raw.size}", compressed.size < raw.size)
        assertEquals(24, FrameCodecs.openZlFrameFormatVersion(compressed))
        assertArrayEquals(raw, codec.decompress(compressed))
        assertArrayEquals(asset("frame.openzl"), compressed)
    }

    @Test
    fun zstdFallbackRoundTrips() {
        val codec = ZstdCompressor()
        val raw = asset("frame.bin")
        val compressed = codec.compress(raw)
        assertEquals(raw.size.toLong(), com.github.luben.zstd.Zstd.getFrameContentSize(compressed))
        assertArrayEquals(raw, com.github.luben.zstd.Zstd.decompress(compressed, raw.size))
    }

    /** Writes one produced frame, its multipart body and metadata for the cross-language check. */
    @Test
    fun writesProducedFrameForCrossLanguageCheck() {
        assumeTrue(is64Bit())
        val context = instrumentation.targetContext
        val out = File(context.getExternalFilesDir(null), "sensor-stream-produced").apply { mkdirs() }
        val records = goldenRecords()
        val compressor = CodecSelector.forDevice()
        val sorted = AccelRecordPacker.sortStable(records)
        val compressed = compressor.compress(AccelRecordPacker.pack(sorted))
        val meta = FrameMetadata.forFrame("lamp.accelerometer50hz", sorted, compressed, compressor)
        val body = MultipartFrameRequest.body(meta, compressed)
        val buffer = Buffer().also { body.writeTo(it) }

        File(out, "android_frame.openzl").writeBytes(compressed)
        File(out, "android_multipart.bin").writeBytes(buffer.readByteArray())
        File(out, "android_multipart.content-type.txt").writeText(body.contentType().toString())
        val metaMap = LinkedHashMap<String, Any>()
        meta.formFields().forEach { (k, v) -> metaMap[k] = v }
        metaMap["byte_length"] = meta.byteLength
        File(out, "android_meta.json").writeText(GsonBuilder().setPrettyPrinting().create().toJson(metaMap))

        assertEquals("openzl", meta.codec)
        assertEquals(Sha256.hex(compressed), meta.sha256)
        assertTrue(File(out, "android_multipart.bin").length() > compressed.size)
    }
}
