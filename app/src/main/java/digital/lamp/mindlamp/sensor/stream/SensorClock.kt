package digital.lamp.mindlamp.sensor.stream

import kotlin.math.abs

/**
 * Converts `SensorEvent.timestamp` (nanoseconds since boot, the `elapsedRealtimeNanos` base) to
 * Unix epoch milliseconds with one boot-to-epoch offset per collection session.
 *
 * Call [hasDrifted] periodically. When it returns `true`, close the open frames and call
 * [startSession]; timestamps already packed are never rewritten.
 */
class SensorClock(
    private val wallClockMs: () -> Long,
    private val elapsedRealtimeNs: () -> Long,
    private val maxDriftMs: Long = MAX_DRIFT_MS
) {
    private var offsetNs = 0L

    /** The number of sessions started so far. */
    var sessionCount: Int = 0
        private set

    /** Captures a fresh boot-to-epoch offset. */
    fun startSession() {
        offsetNs = wallClockMs() * NANOS_PER_MS - elapsedRealtimeNs()
        sessionCount++
    }

    /** Returns `floor(epoch_ns / 1e6)` for a sensor event time. */
    fun toEpochMs(eventTimestampNs: Long): Long = Math.floorDiv(eventTimestampNs + offsetNs, NANOS_PER_MS)

    /** Returns the wall clock minus the session clock, in milliseconds. */
    fun driftMs(): Long = wallClockMs() - toEpochMs(elapsedRealtimeNs())

    /** Returns `true` when the wall clock moved more than [maxDriftMs] from the session clock. */
    fun hasDrifted(): Boolean = abs(driftMs()) > maxDriftMs

    companion object {
        const val MAX_DRIFT_MS = 1_000L
        private const val NANOS_PER_MS = 1_000_000L
    }
}
