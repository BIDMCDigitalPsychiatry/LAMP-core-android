package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class ForegroundServiceTypesTest {
    private val health = 0x100   // ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
    private val location = 0x8   // ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION

    @Test
    fun healthAlwaysLocationOnlyWithGps1mAndFineLocation() {
        assertEquals(health, ForegroundServiceTypes.select(gps1mConfigured = false, fineLocationGranted = false))
        assertEquals(health, ForegroundServiceTypes.select(gps1mConfigured = false, fineLocationGranted = true))
        assertEquals(health, ForegroundServiceTypes.select(gps1mConfigured = true, fineLocationGranted = false))
        assertEquals(health or location, ForegroundServiceTypes.select(gps1mConfigured = true, fineLocationGranted = true))
    }
}
