package digital.lamp.mindlamp.sensor.stream

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/**
 * Smoke-tests the real `SensorManager` path: register, batch, flush on stop, seal a frame.
 */
@RunWith(AndroidJUnit4::class)
class HighRateAccelerometerCollectorTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun collectsAndSealsAFrameOnStop() {
        val store = HighRateAccelerometerCollector.store(context)
        store.root.deleteRecursively()
        val before = System.currentTimeMillis()

        HighRateAccelerometerCollector.start(context, SensorTier.HZ50)
        HighRateAccelerometerCollector.start(context, SensorTier.HZ50) // idempotent
        assertEquals(SensorTier.HZ50, HighRateAccelerometerCollector.runningTier)
        Thread.sleep(3_000)
        HighRateAccelerometerCollector.stop()
        assertNull(HighRateAccelerometerCollector.runningTier)

        var sealed = store.sealed()
        val deadline = System.currentTimeMillis() + 10_000
        while (sealed.isEmpty() && System.currentTimeMillis() < deadline) {
            Thread.sleep(200)
            sealed = store.sealed()
        }
        assertTrue("no frame sealed", sealed.isNotEmpty())
        assertTrue(store.openFiles().isEmpty())

        for (frame in sealed) {
            val meta = frame.meta
            assertEquals("lamp.accelerometer50hz", meta.sensorSpec)
            val raw = when (meta.codec) {
                FrameCodecs.OPENZL -> OpenZlCompressor().decompress(frame.bytes())
                else -> com.github.luben.zstd.Zstd.decompress(frame.bytes(), meta.sampleCount * 20)
            }
            assertEquals(meta.sampleCount * AccelRecordPacker.RECORD_SIZE, raw.size)
            val records = AccelRecordPacker.unpack(raw)
            assertEquals(meta.startTimestamp, records.first().timestamp)
            assertEquals(meta.endTimestamp, records.last().timestamp)
            assertEquals(records.map { it.timestamp }.sorted(), records.map { it.timestamp })
            assertEquals(FrameWindow.indexOf(meta.startTimestamp), FrameWindow.indexOf(meta.endTimestamp))
            assertTrue("start ${meta.startTimestamp} vs $before", abs(meta.startTimestamp - before) < 60_000)
            assertEquals(Sha256.hex(frame.bytes()), meta.sha256)
        }
        assertTrue(sealed.sumOf { it.meta.sampleCount } > 10)
        store.root.deleteRecursively()
    }
}
