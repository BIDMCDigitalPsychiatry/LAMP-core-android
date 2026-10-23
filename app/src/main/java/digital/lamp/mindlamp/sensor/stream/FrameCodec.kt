package digital.lamp.mindlamp.sensor.stream

import com.github.luben.zstd.Zstd
import java.io.IOException

/**
 * Compresses packed frames for upload. A frame is compressed once; retries resend the same bytes.
 */
interface FrameCompressor {
    /** The multipart `codec` value. */
    val codec: String

    /** The multipart `codec_format_version` value. */
    val formatVersion: Int

    fun compress(raw: ByteArray): ByteArray
}

object FrameCodecs {
    const val OPENZL = "openzl"
    const val ZSTD = "zstd"
    const val OPENZL_FORMAT_VERSION = 24
    const val ZSTD_FORMAT_VERSION = 0
    const val ZSTD_LEVEL = 3
    private const val OPENZL_MAGIC_BASE = 0xD7B1A5C0L

    /** Reads an OpenZL frame's format version from its little-endian magic number. */
    fun openZlFrameFormatVersion(blob: ByteArray): Int {
        require(blob.size >= 4) { "an OpenZL frame has at least 4 bytes" }
        val magic = (blob[0].toLong() and 0xFF) or
            ((blob[1].toLong() and 0xFF) shl 8) or
            ((blob[2].toLong() and 0xFF) shl 16) or
            ((blob[3].toLong() and 0xFF) shl 24)
        return (magic - OPENZL_MAGIC_BASE).toInt()
    }
}

/** OpenZL with the contract's split-by-struct graph, through `libsensorstream.so`. */
class OpenZlCompressor : FrameCompressor {
    override val codec: String get() = FrameCodecs.OPENZL
    override val formatVersion: Int get() = FrameCodecs.OPENZL_FORMAT_VERSION

    override fun compress(raw: ByteArray): ByteArray {
        val error = IntArray(1)
        return OpenZlNative.compress(raw, error) ?: throw IOException("OpenZL compress failed: ${error[0]}")
    }

    /** Decompresses a frame that has exactly one output. */
    fun decompress(blob: ByteArray): ByteArray {
        val error = IntArray(1)
        return OpenZlNative.decompress(blob, error) ?: throw IOException("OpenZL decompress failed: ${error[0]}")
    }
}

/** One-shot zstd at level 3 with the content-size flag set (zstd-jni's default). */
class ZstdCompressor : FrameCompressor {
    override val codec: String get() = FrameCodecs.ZSTD
    override val formatVersion: Int get() = FrameCodecs.ZSTD_FORMAT_VERSION

    override fun compress(raw: ByteArray): ByteArray = Zstd.compress(raw, FrameCodecs.ZSTD_LEVEL)
}

/**
 * Chooses OpenZL when the native library has it, and zstd otherwise (32-bit ABIs).
 */
object CodecSelector {
    fun select(
        openZlAvailable: Boolean,
        openZl: () -> FrameCompressor = { OpenZlCompressor() },
        zstd: () -> FrameCompressor = { ZstdCompressor() }
    ): FrameCompressor = if (openZlAvailable) openZl() else zstd()

    /** Returns the compressor for this device's ABI. */
    fun forDevice(): FrameCompressor = select(OpenZlNative.isUsable())
}
