package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SensorStreamFilesTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val identity = object : FrameCompressor {
        override val codec = "zstd"
        override val formatVersion = 0
        override fun compress(raw: ByteArray) = raw.copyOf()
    }
    private val spec = "lamp.accelerometer50hz"

    private fun sealOneFrame(store: FrameStore, session: String) {
        val partitioner = FramePartitioner(object : FramePartitioner.Listener {
            override fun onRecordsAppended(key: FramePartitioner.FrameKey, records: List<AccelRecord>) =
                store.appendOpen(spec, session, key, records)
            override fun onFrameClosed(frame: FramePartitioner.ClosedFrame) {
                store.seal(spec, session, frame, identity)
            }
        })
        partitioner.accept(Vectors.records())
        partitioner.closeAll()
    }

    @Test
    fun framesBelongToTheParticipantWhoCollectedThem() {
        val files = tmp.newFolder("files")
        sealOneFrame(SensorStreamFiles.frameStore(files, "U111"), "s1")

        assertEquals(1, SensorStreamFiles.frameStore(files, "U111").sealed().size)
        // Another participant who signs in on the same device never sees, or uploads, them.
        assertEquals(0, SensorStreamFiles.frameStore(files, "U222").sealed().size)
        assertEquals(listOf(SensorStreamFiles.frameStore(files, "U111").root), SensorStreamFiles.frameStores(files).map { it.root })
    }

    @Test
    fun gps1mEventsBelongToTheParticipantWhoCollectedThem() {
        val files = tmp.newFolder("files")
        SensorStreamFiles.gps1mQueue(files, "U111").append(Gps1mEvents.toJson(1L, 1.0, 2.0, null, null))
        assertEquals(1, SensorStreamFiles.gps1mQueue(files, "U111").size())
        assertEquals(0, SensorStreamFiles.gps1mQueue(files, "U222").size())
    }

    @Test
    fun ownerDirNamesAreDistinctAndStayInsideTheRoot() {
        val ids = listOf("U3273814263", "u3273814263", "../U1", "a/b", "a_b", ".", "..", "", "ü")
        val names = ids.map { SensorStreamFiles.ownerDirName(it) }
        assertEquals(ids.size, names.toSet().size)
        names.forEach { name ->
            assertFalse(name, name.contains('/') || name.contains('\\'))
            assertTrue(name, name != "." && name != ".." && name.isNotEmpty())
        }
        val files = tmp.newFolder("files")
        ids.forEach { id ->
            val root = SensorStreamFiles.frameStore(files, id).root.canonicalFile
            assertEquals(File(files, SensorStreamFiles.SENSOR_STREAM_DIR).canonicalFile, root.parentFile)
        }
    }

    @Test
    fun clearAllDeletesEveryParticipantsData() {
        val files = tmp.newFolder("files")
        val store = SensorStreamFiles.frameStore(files, "U111")
        sealOneFrame(store, "s1")
        store.appendOpen(spec, "s2", FramePartitioner.FrameKey(1L, 0), Vectors.records().take(3))
        SensorStreamFiles.gps1mQueue(files, "U222").append(Gps1mEvents.toJson(1L, 1.0, 2.0, null, null))

        SensorStreamFiles.clearAll(files)

        assertFalse(File(files, SensorStreamFiles.SENSOR_STREAM_DIR).exists())
        assertFalse(File(files, SensorStreamFiles.GPS1M_DIR).exists())
        assertEquals(0, SensorStreamFiles.frameStore(files, "U111").sealed().size)
        assertEquals(0, SensorStreamFiles.gps1mQueue(files, "U222").size())
        assertEquals(0, SensorStreamFiles.frameStores(files).size)
        SensorStreamFiles.clearAll(files) // nothing left: still fine
    }
}
