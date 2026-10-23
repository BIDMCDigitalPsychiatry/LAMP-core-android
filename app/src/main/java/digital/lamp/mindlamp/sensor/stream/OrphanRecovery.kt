package digital.lamp.mindlamp.sensor.stream

import java.io.File

/**
 * Seals the open frames that dead processes left in every participant's [FrameStore], once
 * per process. Both the collector and the upload worker call it, so recovery runs even when
 * no accelerometer tier is configured any more.
 *
 * @param liveProcessId the id of this process; its own open frames are left alone.
 */
class OrphanRecovery(private val liveProcessId: String) {
    private var done = false

    /** The last failure, or `null` when the last run succeeded. */
    var lastError: Exception? = null
        private set
    /**
     * Recovers every participant's orphaned frames into that participant's store and returns
     * them. Later calls return an empty list once a run completes without errors.
     */
    @Synchronized
    fun recoverOnce(filesDir: File, compressor: FrameCompressor): List<FrameStore.SealedFrame> {
        if (done) return emptyList()
        val recovered = mutableListOf<FrameStore.SealedFrame>()
        var failed: Exception? = null
        for (store in SensorStreamFiles.frameStores(filesDir)) {
            // A failure in one participant's store doesn't stop the others.
            try {
                recovered += store.recoverOpenFrames(liveProcessId, compressor)
            } catch (e: Exception) {
                failed = e
            }
        }
        lastError = failed
        done = failed == null
        return recovered
    }
}
