package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SensorClockTest {
    private var wall = 1_790_000_100_000L
    private var elapsed = 5_000_000_000L
    private val clock = SensorClock({ wall }, { elapsed })

    @Test
    fun convertsBootNanosToEpochMillisWithFloor() {
        clock.startSession()
        assertEquals(1_790_000_100_000L, clock.toEpochMs(5_000_000_000L))
        assertEquals(1_790_000_100_020L, clock.toEpochMs(5_020_999_999L))
        assertEquals(1_790_000_099_999L, clock.toEpochMs(4_999_999_999L))
        assertEquals(1, clock.sessionCount)
    }

    @Test
    fun reusesSessionOffsetForLaterSamples() {
        clock.startSession()
        wall += 250
        elapsed += 10_000_000_000L
        assertEquals(1_790_000_110_000L, clock.toEpochMs(15_000_000_000L))
    }

    @Test
    fun driftOverOneSecondStartsNewSession() {
        clock.startSession()
        elapsed += 60_000_000_000L
        wall += 60_000L + 1_000L
        assertEquals(1_000L, clock.driftMs())
        assertFalse(clock.hasDrifted())
        wall += 1
        assertTrue(clock.hasDrifted())
        clock.startSession()
        assertEquals(2, clock.sessionCount)
        assertEquals(0L, clock.driftMs())
        assertEquals(wall, clock.toEpochMs(elapsed))
    }

    @Test
    fun negativeDriftAlsoCounts() {
        clock.startSession()
        wall -= 1_001L
        assertTrue(clock.hasDrifted())
    }
}
