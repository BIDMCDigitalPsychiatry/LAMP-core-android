package digital.lamp.mindlamp.sensor.stream

/**
 * JNI bindings to `libsensorstream.so` (OpenZL v0.3.0 and the `lamp_ozl` shim).
 *
 * On 32-bit ABIs the library is a stub that reports OpenZL as unavailable. Where the library
 * can't load at all (for example, JVM unit tests), [isLibraryLoaded] is `false`; check
 * [isUsable] before calling the native methods.
 */
object OpenZlNative {
    /** `true` when `libsensorstream.so` loaded. */
    @JvmStatic
    val isLibraryLoaded: Boolean = try {
        System.loadLibrary("sensorstream")
        true
    } catch (e: Throwable) {
        false
    }

    /** Returns `true` when the native library loaded and was built with OpenZL. */
    fun isUsable(): Boolean = isLibraryLoaded && isAvailable()

    /** Returns `true` when the library was built with OpenZL (64-bit ABIs). */
    @JvmStatic
    external fun isAvailable(): Boolean

    /** Returns the OpenZL frame format version the library writes (`24`). */
    @JvmStatic
    external fun formatVersion(): Int

    /** Compresses packed records; returns `null` and sets `errorOut[0]` on failure. */
    @JvmStatic
    external fun compress(input: ByteArray, errorOut: IntArray): ByteArray?

    /** Decompresses one single-output frame; returns `null` and sets `errorOut[0]` on failure. */
    @JvmStatic
    external fun decompress(input: ByteArray, errorOut: IntArray): ByteArray?
}
