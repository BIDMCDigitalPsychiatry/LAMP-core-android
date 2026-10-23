package digital.lamp.mindlamp.sensor.stream

import com.google.gson.JsonParser
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FrameStoreTest {
    @get:Rule
    val tmp = TemporaryFolder()

    /** Identity compressor so tests can compare stored bytes to packed records. */
    private val identity = object : FrameCompressor {
        override val codec = "zstd"
        override val formatVersion = 0
        override fun compress(raw: ByteArray) = raw.copyOf()
    }
    private val spec = "lamp.accelerometer50hz"

    private fun store() = FrameStore(tmp.newFolder("sensor_stream"))

    private fun closeVectorFrames(store: FrameStore, session: String): List<FrameStore.SealedFrame> {
        val sealed = mutableListOf<FrameStore.SealedFrame>()
        val partitioner = FramePartitioner(object : FramePartitioner.Listener {
            override fun onRecordsAppended(key: FramePartitioner.FrameKey, records: List<AccelRecord>) =
                store.appendOpen(spec, session, key, records)
            override fun onFrameClosed(frame: FramePartitioner.ClosedFrame) {
                sealed.add(store.seal(spec, session, frame, identity))
            }
        })
        val shuffled = Vectors.records().shuffled(java.util.Random(3))
        shuffled.chunked(97).forEach { partitioner.accept(it) }
        assertEquals(1, store.openFiles().size)
        partitioner.closeAll()
        return sealed
    }

    @Test
    fun sealsGoldenFrameWithSidecarMetadata() {
        val store = store()
        val sealed = closeVectorFrames(store, "s1")
        assertEquals(1, sealed.size)
        val frame = sealed[0]
        assertArrayEquals(Vectors.bytes("frame.bin"), frame.bytes())
        assertEquals(0, store.openFiles().size)

        val json = JsonParser().parse(frame.metaFile.readText()).asJsonObject
        assertEquals(spec, json["sensor_spec"].asString)
        assertEquals(1_790_000_100_000L, json["start_timestamp"].asLong)
        assertEquals(1_790_000_109_980L, json["end_timestamp"].asLong)
        assertEquals(500, json["sample_count"].asInt)
        assertEquals("zstd", json["codec"].asString)
        assertEquals(0, json["codec_format_version"].asInt)
        assertEquals("android", json["platform"].asString)
        assertEquals("m/s^2", json["unit"].asString)
        assertEquals(Sha256.hex(Vectors.bytes("frame.bin")), json["sha256"].asString)
        assertEquals(10_000, json["byte_length"].asInt)

        val listed = store.sealed()
        assertEquals(1, listed.size)
        assertEquals(frame.meta, listed[0].meta)
    }

    @Test
    fun deleteAndQuarantineRemoveFromQueue() {
        val store = store()
        val a = closeVectorFrames(store, "s1")[0]
        val b = closeVectorFrames(store, "s2")[0]
        assertEquals(2, store.sealed().size)
        store.quarantine(a)
        assertEquals(listOf(b.id), store.sealed().map { it.id })
        assertEquals(2, store.quarantined().size)
        store.delete(b)
        assertEquals(0, store.sealed().size)
        assertTrue(!b.frameFile.exists() && !b.metaFile.exists())
    }

    @Test
    fun sealedFrameWithoutMetadataIsIgnored() {
        val store = store()
        val frame = closeVectorFrames(store, "s1")[0]
        frame.metaFile.delete()
        assertEquals(0, store.sealed().size)
    }

    @Test
    fun recoversOrphanOpenFramesAfterProcessDeath() {
        val store = store()
        val case = Vectors.edgeCases()["window_split"].asJsonObject
        val arrivals = case["arrivals"].asJsonArray.map { Vectors.recordRows(it.asJsonArray) }
        // A dead session left raw records for two windows; the live session has its own.
        store.appendOpen(spec, "dead", FramePartitioner.FrameKey(5_966_667L, 0), arrivals[0].take(2))
        store.appendOpen(spec, "dead", FramePartitioner.FrameKey(5_966_668L, 1), arrivals[0].drop(2))
        store.appendOpen(spec, "live", FramePartitioner.FrameKey(5_966_668L, 0), arrivals[1].drop(1))

        val recovered = store.recoverOpenFrames("live", identity)
        assertEquals(2, recovered.size)
        assertEquals(listOf(1_790_000_399_960L, 1_790_000_400_000L), recovered.map { it.meta.startTimestamp }.sorted())
        assertEquals(1, store.openFiles().size)
        assertEquals(2, store.sealed().size)
        store.sealed().forEach { assertEquals(it.meta.sampleCount * 20, it.bytes().size) }
    }
}
