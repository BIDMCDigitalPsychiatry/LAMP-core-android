package digital.lamp.mindlamp.sensor.stream

/**
 * Compresses packed frames for upload.
 */
interface FrameCompressor {
    val codec: String
    val formatVersion: Int
    fun compress(raw: ByteArray): ByteArray
}

object FrameCodecs {
    const val OPENZL = "openzl"
    const val ZSTD = "zstd"
    const val OPENZL_FORMAT_VERSION = 24
    const val ZSTD_FORMAT_VERSION = 0
    const val ZSTD_LEVEL = 3

    fun openZlFrameFormatVersion(blob: ByteArray): Int = TODO("not implemented")
}

class OpenZlCompressor : FrameCompressor {
    override val codec: String get() = TODO("not implemented")
    override val formatVersion: Int get() = TODO("not implemented")
    override fun compress(raw: ByteArray): ByteArray = TODO("not implemented")
    fun decompress(blob: ByteArray): ByteArray = TODO("not implemented")
}

class ZstdCompressor : FrameCompressor {
    override val codec: String get() = TODO("not implemented")
    override val formatVersion: Int get() = TODO("not implemented")
    override fun compress(raw: ByteArray): ByteArray = TODO("not implemented")
}

object CodecSelector {
    fun select(
        openZlAvailable: Boolean,
        openZl: () -> FrameCompressor = { OpenZlCompressor() },
        zstd: () -> FrameCompressor = { ZstdCompressor() }
    ): FrameCompressor = TODO("not implemented")

    fun forDevice(): FrameCompressor = TODO("not implemented")
}
