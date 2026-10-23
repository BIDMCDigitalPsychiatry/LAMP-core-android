package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CodecTest {

    @Test
    fun selectsOpenZlWhenAvailable() {
        val compressor = CodecSelector.select(openZlAvailable = true)
        assertEquals("openzl", compressor.codec)
        assertEquals(24, compressor.formatVersion)
    }

    @Test
    fun fallsBackToZstdWhenOpenZlUnavailable() {
        val compressor = CodecSelector.select(openZlAvailable = false)
        assertEquals("zstd", compressor.codec)
        assertEquals(0, compressor.formatVersion)
    }

    @Test
    fun jvmHasNoNativeLibrarySoDeviceSelectionFallsBack() {
        assertFalse(OpenZlNative.isLibraryLoaded)
        assertFalse(OpenZlNative.isUsable())
        assertEquals("zstd", CodecSelector.forDevice().codec)
    }

    @Test
    fun readsFormatVersionFromGoldenFrameMagic() {
        assertEquals(24, FrameCodecs.openZlFrameFormatVersion(Vectors.bytes("frame.openzl")))
        assertEquals(24, FrameCodecs.openZlFrameFormatVersion(Vectors.bytes("frame_single.openzl")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun formatVersionNeedsFourBytes() {
        FrameCodecs.openZlFrameFormatVersion(ByteArray(3))
    }

    @Test
    fun sha256OfGoldenFramesMatchesMeta() {
        val meta = Vectors.meta()
        assertEquals(meta["frame.openzl"].asJsonObject["sha256"].asString, Sha256.hex(Vectors.bytes("frame.openzl")))
        assertEquals(meta["frame.zstd"].asJsonObject["sha256"].asString, Sha256.hex(Vectors.bytes("frame.zstd")))
    }

    @Test
    fun metadataForFrameMatchesGoldenMeta() {
        val want = Vectors.meta()["frame.openzl"].asJsonObject
        val fake = object : FrameCompressor {
            override val codec = "openzl"
            override val formatVersion = 24
            override fun compress(raw: ByteArray) = Vectors.bytes("frame.openzl")
        }
        val compressed = fake.compress(Vectors.bytes("frame.bin"))
        val meta = FrameMetadata.forFrame("lamp.accelerometer50hz", Vectors.records(), compressed, fake)
        val fields = meta.formFields().toMap()
        listOf("sensor_spec", "codec", "platform", "unit", "sha256").forEach {
            assertEquals(it, want[it].asString, fields[it])
        }
        listOf("start_timestamp", "end_timestamp", "sample_count", "codec_format_version").forEach {
            assertEquals(it, want[it].asLong.toString(), fields[it])
        }
        assertEquals(want["byte_length"].asInt, meta.byteLength)
    }
}
