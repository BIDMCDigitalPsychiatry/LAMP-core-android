package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FramePartitionerTest {

    private class Recorder : FramePartitioner.Listener {
        val closed = mutableListOf<FramePartitioner.ClosedFrame>()
        val appended = mutableMapOf<FramePartitioner.FrameKey, MutableList<AccelRecord>>()
        override fun onRecordsAppended(key: FramePartitioner.FrameKey, records: List<AccelRecord>) {
            assertTrue("append after close", closed.none { it.key == key })
            appended.getOrPut(key) { mutableListOf() }.addAll(records)
        }
        override fun onFrameClosed(frame: FramePartitioner.ClosedFrame) {
            closed.add(frame)
        }
    }

    private fun rec(ts: Long) = AccelRecord(ts, 0f, 0f, 9.80665f)

    @Test
    fun windowIndexAndDeadline() {
        assertEquals(5_966_667L, FrameWindow.indexOf(1_790_000_399_999L))
        assertEquals(5_966_668L, FrameWindow.indexOf(1_790_000_400_000L))
        assertEquals(1_790_000_415_000L, FrameWindow.closeDeadlineMs(5_966_667L))
    }

    @Test
    fun windowSplitVector() {
        val case = Vectors.edgeCases()["window_split"].asJsonObject
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        case["arrivals"].asJsonArray.forEach { partitioner.accept(Vectors.recordRows(it.asJsonArray)) }
        partitioner.closeAll()

        val expected = case["expected_frames"].asJsonArray.map { it.asJsonObject }
        assertEquals(expected.size, recorder.closed.size)
        expected.zip(recorder.closed).forEach { (want, got) ->
            assertEquals(want["window"].asLong, got.key.window)
            assertEquals(want["start_timestamp"].asLong, got.startTimestamp)
            assertEquals(want["end_timestamp"].asLong, got.endTimestamp)
            assertEquals(want["timestamps"].asJsonArray.map { it.asLong }, got.records.map { it.timestamp })
            assertEquals(want.has("late") && want["late"].asBoolean, got.late)
            assertEquals(got.records.size, got.sampleCount)
        }
        assertEquals(0, partitioner.openFrameCount)
    }

    @Test
    fun everyRecordIsAppendedBeforeItsFrameCloses() {
        val case = Vectors.edgeCases()["window_split"].asJsonObject
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        case["arrivals"].asJsonArray.forEach { partitioner.accept(Vectors.recordRows(it.asJsonArray)) }
        partitioner.closeAll()
        recorder.closed.forEach { frame ->
            assertEquals(frame.records.map { it.timestamp }.sorted(),
                recorder.appended[frame.key]!!.map { it.timestamp }.sorted())
        }
        assertEquals(6, recorder.appended.values.sumOf { it.size })
    }

    @Test
    fun laterWindowRecordClosesOpenFrame() {
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        partitioner.accept(listOf(rec(1_790_000_399_960L)))
        assertEquals(0, recorder.closed.size)
        partitioner.accept(listOf(rec(1_790_000_400_000L)))
        assertEquals(1, recorder.closed.size)
        assertEquals(5_966_667L, recorder.closed[0].key.window)
        assertEquals(1, partitioner.openFrameCount)
    }

    @Test
    fun wallClockDeadlineClosesFrame() {
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        partitioner.accept(listOf(rec(1_790_000_399_960L)))
        partitioner.closeExpired(1_790_000_414_999L)
        assertEquals(0, recorder.closed.size)
        partitioner.closeExpired(1_790_000_415_000L)
        assertEquals(1, recorder.closed.size)
        assertEquals(0, partitioner.openFrameCount)
    }

    @Test
    fun recordAfterDeadlineStartsLatePartialFrame() {
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        partitioner.accept(listOf(rec(1_790_000_399_000L)))
        partitioner.closeExpired(1_790_000_416_000L)
        partitioner.accept(listOf(rec(1_790_000_399_500L)))
        partitioner.closeAll()
        assertEquals(2, recorder.closed.size)
        assertFalse(recorder.closed[0].late)
        assertTrue(recorder.closed[1].late)
        assertEquals(recorder.closed[0].key.window, recorder.closed[1].key.window)
        assertTrue(recorder.closed[0].key != recorder.closed[1].key)
    }

    @Test
    fun outOfOrderRecordsInOneWindowAreSortedStably() {
        val case = Vectors.edgeCases()["duplicate_ms"].asJsonObject
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        partitioner.accept(Vectors.recordRows(case["input"].asJsonArray))
        partitioner.closeAll()
        assertEquals(1, recorder.closed.size)
        assertEquals(Vectors.recordRows(case["expected_sorted"].asJsonArray), recorder.closed[0].records)
    }

    @Test
    fun fullFrameClosesAtMaxRecordsAndNothingIsDropped() {
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder, maxRecordsPerFrame = 3)
        partitioner.accept((0 until 7).map { rec(1_790_000_100_000L + it) })
        partitioner.closeAll()
        assertEquals(listOf(3, 3, 1), recorder.closed.map { it.sampleCount })
        assertEquals(7, recorder.closed.sumOf { it.sampleCount })
    }

    @Test
    fun closeAllWithNothingOpenIsNoOp() {
        val recorder = Recorder()
        val partitioner = FramePartitioner(recorder)
        partitioner.accept(emptyList())
        partitioner.closeAll()
        assertEquals(0, recorder.closed.size)
    }
}
