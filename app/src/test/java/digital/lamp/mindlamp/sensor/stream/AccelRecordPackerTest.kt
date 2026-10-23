package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class AccelRecordPackerTest {

    @Test
    fun packsGoldenRecordsByteIdenticalToFrameBin() {
        val packed = AccelRecordPacker.pack(Vectors.records())
        assertEquals(10_000, packed.size)
        assertArrayEquals(Vectors.bytes("frame.bin"), packed)
    }

    @Test
    fun sortsShuffledRecordsBeforePacking() {
        val shuffled = Vectors.records().shuffled(java.util.Random(7))
        assertArrayEquals(Vectors.bytes("frame.bin"), AccelRecordPacker.pack(shuffled))
    }

    @Test
    fun packsSingleRecordFrame() {
        val single = Vectors.bytes("frame_single.bin")
        val records = AccelRecordPacker.unpack(single)
        assertEquals(1, records.size)
        assertArrayEquals(single, AccelRecordPacker.pack(records))
    }

    @Test
    fun keepsDuplicateTimestampsInStableOrder() {
        val case = Vectors.edgeCases()["duplicate_ms"].asJsonObject
        val input = Vectors.recordRows(case["input"].asJsonArray)
        val expected = Vectors.recordRows(case["expected_sorted"].asJsonArray)
        assertEquals(expected, AccelRecordPacker.sortStable(input))
        assertEquals(case["expected_packed_hex"].asString, Vectors.hex(AccelRecordPacker.pack(input)))
    }

    @Test
    fun packUnsortedKeepsArrivalOrderAndUnpackRoundTrips() {
        val case = Vectors.edgeCases()["duplicate_ms"].asJsonObject
        val input = Vectors.recordRows(case["input"].asJsonArray)
        val raw = AccelRecordPacker.packUnsorted(input)
        assertEquals(input.size * AccelRecordPacker.RECORD_SIZE, raw.size)
        assertEquals(input, AccelRecordPacker.unpack(raw))
    }

    @Test(expected = IllegalArgumentException::class)
    fun unpackRejectsPartialRecord() {
        AccelRecordPacker.unpack(ByteArray(21))
    }

    @Test
    fun androidValuesPassThroughUnchangedAtRest() {
        val atRest = Vectors.edgeCases()["units_at_rest"].asJsonObject["android_at_rest_face_up_ms2"].asJsonArray
        val values = floatArrayOf(atRest[0].asFloat, atRest[1].asFloat, atRest[2].asFloat)
        val record = AccelRecord.fromSensorValues(1_790_000_100_000L, values)
        assertEquals(0.0f, record.x)
        assertEquals(0.0f, record.y)
        assertEquals(java.lang.Float.floatToIntBits(9.80665f), java.lang.Float.floatToIntBits(record.z))
        assertEquals(1_790_000_100_000L, record.timestamp)
    }
}
