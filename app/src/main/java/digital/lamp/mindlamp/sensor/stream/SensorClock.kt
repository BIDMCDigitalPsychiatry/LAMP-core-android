package digital.lamp.mindlamp.sensor.stream

/**
 * Converts `SensorEvent.timestamp` (boot nanoseconds) to Unix epoch milliseconds.
 */
class SensorClock(
    private val wallClockMs: () -> Long,
    private val elapsedRealtimeNs: () -> Long,
    private val maxDriftMs: Long = MAX_DRIFT_MS
) {
    val sessionCount: Int get() = TODO("not implemented")

    fun startSession(): Unit = TODO("not implemented")

    fun toEpochMs(eventTimestampNs: Long): Long = TODO("not implemented")

    fun driftMs(): Long = TODO("not implemented")

    fun hasDrifted(): Boolean = TODO("not implemented")

    companion object {
        const val MAX_DRIFT_MS = 1_000L
    }
}
