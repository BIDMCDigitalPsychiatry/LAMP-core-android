package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class OrphanRecoveryTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val identity = object : FrameCompressor {
        override val codec = "zstd"
        override val formatVersion = 0
        override fun compress(raw: ByteArray) = raw.copyOf()
    }
    private val spec = "lamp.accelerometer50hz"
    private val window = FrameWindow.indexOf(Vectors.records().first().timestamp)

    @Test
    fun recoversEveryParticipantsOrphansWithoutACollector() {
        val files = tmp.newFolder("files")
        val a = SensorStreamFiles.frameStore(files, "U111")
        val b = SensorStreamFiles.frameStore(files, "U222")
        // A dead process left open frames for two participants; this process has its own.
        a.appendOpen(spec, "dead-0", FramePartitioner.FrameKey(window, 0), Vectors.records().take(5))
        b.appendOpen(spec, "dead-1", FramePartitioner.FrameKey(window, 0), Vectors.records().take(7))
        a.appendOpen(spec, "live-0", FramePartitioner.FrameKey(window, 1), Vectors.records().take(3))

        val recovered = OrphanRecovery("live").recoverOnce(files, identity)

        assertEquals(2, recovered.size)
        assertEquals(listOf(5), a.sealed().map { it.meta.sampleCount })
        assertEquals(listOf(7), b.sealed().map { it.meta.sampleCount })
        assertEquals(1, a.openFiles().size)
        assertEquals(0, b.openFiles().size)
    }

    @Test
    fun runsOncePerProcess() {
        val files = tmp.newFolder("files")
        val a = SensorStreamFiles.frameStore(files, "U111")
        a.appendOpen(spec, "dead-0", FramePartitioner.FrameKey(window, 0), Vectors.records().take(5))
        val recovery = OrphanRecovery("live")
        assertEquals(1, recovery.recoverOnce(files, identity).size)
        a.appendOpen(spec, "dead-0", FramePartitioner.FrameKey(window, 1), Vectors.records().take(2))
        assertEquals(0, recovery.recoverOnce(files, identity).size)
    }

    @Test
    fun aFailedRunIsRetried() {
        val files = tmp.newFolder("files")
        val a = SensorStreamFiles.frameStore(files, "U111")
        a.appendOpen(spec, "dead-0", FramePartitioner.FrameKey(window, 0), Vectors.records().take(5))
        val failing = object : FrameCompressor {
            override val codec = "zstd"
            override val formatVersion = 0
            override fun compress(raw: ByteArray): ByteArray = throw java.io.IOException("disk")
        }
        val recovery = OrphanRecovery("live")
        assertEquals(0, recovery.recoverOnce(files, failing).size)
        assertEquals(1, a.openFiles().size)
        assertEquals(1, recovery.recoverOnce(files, identity).size)
        assertEquals(0, a.openFiles().size)
    }
}
