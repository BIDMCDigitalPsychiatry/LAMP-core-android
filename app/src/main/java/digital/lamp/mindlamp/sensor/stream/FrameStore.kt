package digital.lamp.mindlamp.sensor.stream

import java.io.File

/**
 * A file queue of accelerometer frames under `filesDir/sensor_stream`.
 */
class FrameStore(val root: File) {
    class SealedFrame(val id: String, val frameFile: File, val metaFile: File, val meta: FrameMetadata) {
        fun bytes(): ByteArray = frameFile.readBytes()
    }

    fun appendOpen(sensorSpec: String, sessionId: String, key: FramePartitioner.FrameKey, records: List<AccelRecord>): Unit = TODO("not implemented")

    fun openFiles(): List<File> = TODO("not implemented")

    fun seal(sensorSpec: String, sessionId: String, frame: FramePartitioner.ClosedFrame, compressor: FrameCompressor): SealedFrame = TODO("not implemented")

    fun sealed(): List<SealedFrame> = TODO("not implemented")

    fun delete(frame: SealedFrame): Unit = TODO("not implemented")

    fun quarantine(frame: SealedFrame): Unit = TODO("not implemented")

    fun quarantined(): List<File> = TODO("not implemented")

    fun recoverOpenFrames(activeSessionId: String?, compressor: FrameCompressor): List<SealedFrame> = TODO("not implemented")
}
