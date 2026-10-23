package digital.lamp.mindlamp.sensor.stream

import com.google.android.gms.location.LocationRequest
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class Gps1mTest {
    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun requestIsOneHighAccuracyFixPerMinute() {
        assertEquals(60_000L, Gps1mRequest.INTERVAL_MS)
        assertEquals(60_000L, Gps1mRequest.FASTEST_INTERVAL_MS)
        assertEquals(LocationRequest.PRIORITY_HIGH_ACCURACY, Gps1mRequest.priority)
        val request = Gps1mRequest.create()
        assertEquals(60_000L, request.interval)
        assertEquals(60_000L, request.fastestInterval)
        assertEquals(LocationRequest.PRIORITY_HIGH_ACCURACY, request.priority)
    }

    @Test
    fun eventHasGpsDataShape() {
        val json = Gps1mEvents.toJson(1_790_000_160_000L, 42.3389, -71.1066, 12.5, 4.8f)
        val o = JsonParser().parse(json).asJsonObject
        assertEquals(1_790_000_160_000L, o["timestamp"].asLong)
        assertEquals("lamp.gps1m", o["sensor"].asString)
        val data = o["data"].asJsonObject
        assertEquals(setOf("latitude", "longitude", "altitude", "accuracy"), data.keySet())
        assertEquals(42.3389, data["latitude"].asDouble, 0.0)
        assertEquals(-71.1066, data["longitude"].asDouble, 0.0)
        assertEquals(12.5, data["altitude"].asDouble, 0.0)
        assertEquals(4.8, data["accuracy"].asDouble, 1e-6)
    }

    @Test
    fun missingAltitudeAndAccuracyAreOmitted() {
        val o = JsonParser().parse(Gps1mEvents.toJson(1L, 1.0, 2.0, null, null)).asJsonObject
        assertEquals(setOf("latitude", "longitude"), o["data"].asJsonObject.keySet())
    }

    @Test
    fun jsonArrayWrapsEvents() {
        val a = Gps1mEvents.toJson(1L, 1.0, 2.0, null, null)
        val b = Gps1mEvents.toJson(2L, 3.0, 4.0, null, null)
        val array = JsonParser().parse(Gps1mEvents.toJsonArray(listOf(a, b))).asJsonArray
        assertEquals(2, array.size())
        assertEquals(2L, array[1].asJsonObject["timestamp"].asLong)
    }

    @Test
    fun queueKeepsEventsUntilRemoved() {
        val queue = Gps1mQueue(tmp.newFolder("gps1m"))
        val events = (1..5).map { Gps1mEvents.toJson(it.toLong(), 1.0, 2.0, null, null) }
        events.forEach { queue.append(it) }
        assertEquals(5, queue.size())
        val first = queue.next(3)
        assertEquals(events.take(3), first.events)
        assertEquals(5, queue.size())
        queue.remove(first)
        assertEquals(2, queue.size())
        assertEquals(events.drop(3), queue.next(10).events)
        assertTrue(first.files.none { it.exists() })
        assertFalse(queue.dir.listFiles()!!.any { it.name.endsWith(".tmp") })
    }
}
