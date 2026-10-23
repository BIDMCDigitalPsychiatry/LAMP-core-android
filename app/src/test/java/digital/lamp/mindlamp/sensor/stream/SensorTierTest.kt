package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SensorTierTest {

    @Test
    fun mapsEverySpecToSamplingPeriodAndLatency() {
        val expected = mapOf(
            "lamp.accelerometer10hz" to Triple(10, 100_000, 5_000_000),
            "lamp.accelerometer20hz" to Triple(20, 50_000, 5_000_000),
            "lamp.accelerometer50hz" to Triple(50, 20_000, 5_000_000),
            "lamp.accelerometer100hz" to Triple(100, 10_000, 5_000_000)
        )
        expected.forEach { (spec, values) ->
            val tier = SensorTier.fromSpec(spec)!!
            assertEquals(spec, tier.spec)
            assertEquals(values.first, tier.nominalHz)
            assertEquals(values.second, tier.samplingPeriodUs)
            assertEquals(values.third, tier.maxReportLatencyUs)
        }
        assertEquals(4, SensorTier.values().size)
    }

    @Test
    fun nominalSamplesPerWindowMatchesVector() {
        val nominal = Vectors.edgeCases()["sample_count_limits"].asJsonObject["nominal_per_window"].asJsonObject
        nominal.entrySet().forEach { (spec, count) ->
            assertEquals(spec, count.asInt, SensorTier.fromSpec(spec)!!.nominalSamplesPerWindow)
        }
    }

    @Test
    fun unknownSpecsAreNotTiers() {
        assertNull(SensorTier.fromSpec("lamp.accelerometer"))
        assertNull(SensorTier.fromSpec("lamp.gps1m"))
        assertNull(SensorTier.fromSpec(null))
    }

    @Test
    fun highestConfiguredTierWins() {
        assertEquals(
            SensorTier.HZ50,
            SensorTier.highestOf(listOf("lamp.gps", "lamp.accelerometer10hz", null, "lamp.accelerometer50hz"))
        )
        assertNull(SensorTier.highestOf(listOf("lamp.accelerometer", "lamp.gps")))
    }
}
