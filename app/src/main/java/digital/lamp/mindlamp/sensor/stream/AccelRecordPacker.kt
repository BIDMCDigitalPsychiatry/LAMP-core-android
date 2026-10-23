package digital.lamp.mindlamp.sensor.stream

/**
 * Packs accelerometer records into the 20-byte little-endian wire format.
 */
object AccelRecordPacker {
    const val RECORD_SIZE = 20

    fun sortStable(records: List<AccelRecord>): List<AccelRecord> = TODO("not implemented")

    fun pack(records: List<AccelRecord>): ByteArray = TODO("not implemented")

    fun packUnsorted(records: List<AccelRecord>): ByteArray = TODO("not implemented")

    fun unpack(bytes: ByteArray): List<AccelRecord> = TODO("not implemented")
}
