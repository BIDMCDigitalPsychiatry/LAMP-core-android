package digital.lamp.mindlamp.sensor.stream

/**
 * Wall-clock window arithmetic for sensor-stream frames.
 */
object FrameWindow {
    const val WINDOW_MS = 300_000L

    /** 5 s maximum report latency plus a 10 s margin. */
    const val CLOSE_MARGIN_MS = 15_000L

    /** Returns `k = floor(timestamp / 300000)`. */
    fun indexOf(timestamp: Long): Long = Math.floorDiv(timestamp, WINDOW_MS)

    /** Returns the wall-clock time at which an open frame for [window] must close. */
    fun closeDeadlineMs(window: Long): Long = (window + 1) * WINDOW_MS + CLOSE_MARGIN_MS
}

/**
 * Splits accelerometer records into frames, one or more per 5-minute window.
 *
 * A frame closes when a record from a later window arrives, when [closeExpired] passes the
 * window's deadline, when it reaches [maxRecordsPerFrame], or on [closeAll]. A record for a
 * window whose frame already closed starts a new partial frame; no record is dropped. When
 * [Listener.onFrameClosed] throws, the frame goes to [Listener.onFrameCloseFailed] and the rest
 * of the batch is still processed.
 * This class isn't thread-safe; call it from one thread.
 */
class FramePartitioner(
    private val listener: Listener,
    private val maxRecordsPerFrame: Int = MAX_RECORDS_PER_FRAME
) {
    /** Identifies one frame: its window and a per-partitioner sequence number. */
    data class FrameKey(val window: Long, val sequence: Int)

    /** A closed frame. [records] are stably sorted by timestamp and never empty. */
    class ClosedFrame(val key: FrameKey, val records: List<AccelRecord>, val late: Boolean) {
        val startTimestamp: Long get() = records.first().timestamp
        val endTimestamp: Long get() = records.last().timestamp
        val sampleCount: Int get() = records.size
    }

    interface Listener {
        /** Receives records as they join a frame, in arrival order, before the frame closes. */
        fun onRecordsAppended(key: FrameKey, records: List<AccelRecord>) {}

        /** Receives each frame once, after its last record. */
        fun onFrameClosed(frame: ClosedFrame)

        /**
         * Receives a frame whose [onFrameClosed] threw. Its records already reached
         * [onRecordsAppended], so you can recover them later.
         */
        fun onFrameCloseFailed(frame: ClosedFrame, error: Exception) {}
    }

    private class OpenFrame(val key: FrameKey, val late: Boolean) {
        val records = ArrayList<AccelRecord>()
        val pending = ArrayList<AccelRecord>()
    }

    private val open = LinkedHashMap<Long, OpenFrame>()
    private var nextSequence = 0
    private var highestClosedWindow = Long.MIN_VALUE

    val openFrameCount: Int get() = open.size

    /** Adds one delivered batch, splitting it by window record by record. */
    fun accept(batch: List<AccelRecord>) {
        for (record in batch) {
            val window = FrameWindow.indexOf(record.timestamp)
            closeWhere { it.key.window < window }
            val frame = open.getOrPut(window) {
                OpenFrame(FrameKey(window, nextSequence++), late = window <= highestClosedWindow)
            }
            frame.records.add(record)
            frame.pending.add(record)
            if (frame.records.size >= maxRecordsPerFrame) close(frame)
        }
        flushPending()
    }

    /** Closes every frame whose window deadline is at or before [nowWallMs]. */
    fun closeExpired(nowWallMs: Long) {
        closeWhere { nowWallMs >= FrameWindow.closeDeadlineMs(it.key.window) }
    }

    /** Closes every open frame, oldest window first. Call it when the collector stops. */
    fun closeAll() {
        closeWhere { true }
    }

    private fun closeWhere(predicate: (OpenFrame) -> Boolean) {
        open.values.filter(predicate).sortedWith(compareBy({ it.key.window }, { it.key.sequence })).forEach { close(it) }
    }

    private fun close(frame: OpenFrame) {
        flushPending(frame)
        open.remove(frame.key.window)
        highestClosedWindow = maxOf(highestClosedWindow, frame.key.window)
        val closed = ClosedFrame(frame.key, AccelRecordPacker.sortStable(frame.records), frame.late)
        // One failed seal mustn't abort the batch or the other frames being closed.
        try {
            listener.onFrameClosed(closed)
        } catch (e: Exception) {
            listener.onFrameCloseFailed(closed, e)
        }
    }

    private fun flushPending() {
        open.values.forEach { flushPending(it) }
    }

    private fun flushPending(frame: OpenFrame) {
        if (frame.pending.isEmpty()) return
        val appended = ArrayList(frame.pending)
        frame.pending.clear()
        listener.onRecordsAppended(frame.key, appended)
    }

    companion object {
        /** The contract's `sample_count` upper bound (300 s × 200 Hz × 1.1). */
        const val MAX_RECORDS_PER_FRAME = 66_000
    }
}
