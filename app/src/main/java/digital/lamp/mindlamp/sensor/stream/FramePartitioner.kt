package digital.lamp.mindlamp.sensor.stream

/**
 * Wall-clock window arithmetic for sensor-stream frames.
 */
object FrameWindow {
    const val WINDOW_MS = 300_000L
    const val CLOSE_MARGIN_MS = 15_000L

    fun indexOf(timestamp: Long): Long = TODO("not implemented")

    fun closeDeadlineMs(window: Long): Long = TODO("not implemented")
}

/**
 * Splits accelerometer records into frames, one or more per 5-minute window.
 */
class FramePartitioner(
    private val listener: Listener,
    private val maxRecordsPerFrame: Int = MAX_RECORDS_PER_FRAME
) {
    data class FrameKey(val window: Long, val sequence: Int)

    class ClosedFrame(val key: FrameKey, val records: List<AccelRecord>, val late: Boolean) {
        val startTimestamp: Long get() = records.first().timestamp
        val endTimestamp: Long get() = records.last().timestamp
        val sampleCount: Int get() = records.size
    }

    interface Listener {
        fun onRecordsAppended(key: FrameKey, records: List<AccelRecord>) {}
        fun onFrameClosed(frame: ClosedFrame)
    }

    val openFrameCount: Int get() = TODO("not implemented")

    fun accept(batch: List<AccelRecord>): Unit = TODO("not implemented")

    fun closeExpired(nowWallMs: Long): Unit = TODO("not implemented")

    fun closeAll(): Unit = TODO("not implemented")

    companion object {
        const val MAX_RECORDS_PER_FRAME = 66_000
    }
}
