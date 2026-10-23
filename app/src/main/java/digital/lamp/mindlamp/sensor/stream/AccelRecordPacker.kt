package digital.lamp.mindlamp.sensor.stream

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Packs accelerometer records into the 20-byte little-endian wire format:
 * `int64 timestamp, float32 x, float32 y, float32 z`.
 */
object AccelRecordPacker {
    const val RECORD_SIZE = 20

    /** Sorts by timestamp, keeping the arrival order of equal timestamps. */
    fun sortStable(records: List<AccelRecord>): List<AccelRecord> = records.sortedBy { it.timestamp }

    /** Sorts [records] stably and packs them as one frame. */
    fun pack(records: List<AccelRecord>): ByteArray = packUnsorted(sortStable(records))

    /** Packs [records] in the given order, for append-as-you-go storage. */
    fun packUnsorted(records: List<AccelRecord>): ByteArray {
        val buffer = ByteBuffer.allocate(records.size * RECORD_SIZE).order(ByteOrder.LITTLE_ENDIAN)
        for (r in records) {
            buffer.putLong(r.timestamp).putFloat(r.x).putFloat(r.y).putFloat(r.z)
        }
        return buffer.array()
    }

    /** Unpacks records in stored order. Throws [IllegalArgumentException] on a partial record. */
    fun unpack(bytes: ByteArray): List<AccelRecord> {
        require(bytes.size % RECORD_SIZE == 0) { "length ${bytes.size} is not a multiple of $RECORD_SIZE" }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return List(bytes.size / RECORD_SIZE) {
            AccelRecord(buffer.long, buffer.float, buffer.float, buffer.float)
        }
    }
}
