package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Mirrors the iOS `SensorStreamRoutingTests`. */
class SensorStreamRoutingTest {

    @Test
    fun noSensorStreamSpecsKeepsLegacyBehaviour() {
        val routing = SensorStreamRouting.from(listOf("lamp.accelerometer", "lamp.gps", "lamp.device_motion"))
        assertNull(routing.accelerometerTier)
        assertTrue(routing.runsLegacyAccelerometer)
        assertTrue(routing.runsLegacyDeviceMotion)
        assertFalse(routing.runsGps1m)
        assertEquals(emptyList<String>(), routing.ignoredAccelerometerSpecs)
        assertEquals(setOf("lamp.accelerometer", "lamp.device_motion"), routing.legacyMotionSpecs)
    }

    @Test
    fun tierReplacesLegacyAccelerometer() {
        val routing = SensorStreamRouting.from(listOf("lamp.accelerometer", "lamp.accelerometer10hz"))
        assertEquals(SensorTier.HZ10, routing.accelerometerTier)
        assertFalse(routing.runsLegacyAccelerometer)
        assertFalse(routing.runsLegacyDeviceMotion)
        assertEquals(emptySet<String>(), routing.legacyMotionSpecs)
    }

    @Test
    fun tierKeepsDeviceMotion() {
        val routing = SensorStreamRouting.from(listOf("lamp.device_motion", "lamp.accelerometer10hz"))
        assertEquals(SensorTier.HZ10, routing.accelerometerTier)
        assertTrue(routing.runsLegacyDeviceMotion)
        assertFalse(routing.runsLegacyAccelerometer)
        assertEquals(setOf("lamp.device_motion"), routing.legacyMotionSpecs)

        val all = SensorStreamRouting.from(listOf("lamp.accelerometer", "lamp.device_motion", "lamp.accelerometer50hz"))
        assertEquals(setOf("lamp.device_motion"), all.legacyMotionSpecs)
    }

    @Test
    fun fastestTierWinsAndOthersAreIgnored() {
        val routing = SensorStreamRouting.from(
            listOf("lamp.accelerometer20hz", "lamp.accelerometer100hz", "lamp.accelerometer10hz", "lamp.accelerometer")
        )
        assertEquals(SensorTier.HZ100, routing.accelerometerTier)
        assertEquals(listOf("lamp.accelerometer10hz", "lamp.accelerometer20hz"), routing.ignoredAccelerometerSpecs)
        assertFalse(routing.runsLegacyAccelerometer)
    }

    @Test
    fun eachTierAloneIsRouted() {
        SensorTier.values().forEach { tier ->
            val routing = SensorStreamRouting.from(listOf(tier.spec))
            assertEquals(tier, routing.accelerometerTier)
            assertFalse(routing.runsLegacyAccelerometer)
        }
    }

    @Test
    fun gps1mRunsAlongsideLegacyGps() {
        assertTrue(SensorStreamRouting.from(listOf("lamp.gps", "lamp.gps1m")).runsGps1m)
        assertTrue(SensorStreamRouting.from(listOf("lamp.gps1m")).runsGps1m)
        assertNull(SensorStreamRouting.from(listOf("lamp.gps", "lamp.gps1m")).accelerometerTier)
    }

    @Test
    fun unknownSpecsAreIgnored() {
        val routing = SensorStreamRouting.from(listOf("lamp.accelerometer200hz", "lamp.gps5m", "custom.sensor", null))
        assertNull(routing.accelerometerTier)
        assertFalse(routing.runsGps1m)
        assertFalse(routing.runsLegacyAccelerometer)
        assertFalse(routing.runsLegacyDeviceMotion)
    }
}
