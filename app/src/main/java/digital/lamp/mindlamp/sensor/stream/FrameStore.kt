package digital.lamp.mindlamp.sensor.stream

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileOutputStream

/**
 * A file queue of accelerometer frames, normally under `filesDir/sensor_stream`.
 *
 * - `open/` holds the raw 20-byte records of each open frame, appended as they arrive, so a
 *   process death loses at most the records not yet appended.
 * - `sealed/` holds each closed frame as `<id>.frame` (the compressed bytes, written once) and
 *   `<id>.json` (its metadata). The JSON file is written last and marks the frame complete.
 * - `quarantine/` holds frames the server rejected for good (`400`, `409`, `413`).
 *
 * Calls aren't synchronized; use one writer thread, and let the upload worker only read,
 * delete and quarantine sealed frames.
 */
class FrameStore(val root: File) {
    class SealedFrame(val id: String, val frameFile: File, val metaFile: File, val meta: FrameMetadata) {
        fun bytes(): ByteArray = frameFile.readBytes()
    }

    private val openDir = File(root, "open")
    private val sealedDir = File(root, "sealed")
    private val quarantineDir = File(root, "quarantine")
    private val gson = Gson()

    /** Appends [records] to the open frame's raw file. */
    fun appendOpen(sensorSpec: String, sessionId: String, key: FramePartitioner.FrameKey, records: List<AccelRecord>) {
        if (records.isEmpty()) return
        openDir.mkdirs()
        FileOutputStream(openFile(sensorSpec, sessionId, key), true).use {
            it.write(AccelRecordPacker.packUnsorted(records))
            it.fd.sync()
        }
    }

    /** Lists raw files of frames that are still open (or were open when the process died). */
    fun openFiles(): List<File> =
        openDir.listFiles { f -> f.name.endsWith(OPEN_SUFFIX) }?.sortedBy { it.name } ?: emptyList()

    /** Compresses [frame] once, writes it and its metadata, and removes its raw file. */
    fun seal(
        sensorSpec: String,
        sessionId: String,
        frame: FramePartitioner.ClosedFrame,
        compressor: FrameCompressor
    ): SealedFrame {
        val id = frameId(sensorSpec, sessionId, frame.key)
        val sealed = sealRecords(id, sensorSpec, frame.records, compressor)
        openFile(sensorSpec, sessionId, frame.key).delete()
        return sealed
    }

    /** Lists complete sealed frames, oldest first. */
    fun sealed(): List<SealedFrame> {
        val metas = sealedDir.listFiles { f -> f.name.endsWith(META_SUFFIX) } ?: return emptyList()
        return metas.mapNotNull { metaFile ->
            val id = metaFile.name.removeSuffix(META_SUFFIX)
            val frameFile = File(sealedDir, id + FRAME_SUFFIX)
            if (!frameFile.exists()) return@mapNotNull null
            val meta = try {
                readMeta(metaFile)
            } catch (e: Exception) {
                null
            } ?: return@mapNotNull null
            SealedFrame(id, frameFile, metaFile, meta)
        }.sortedWith(compareBy({ it.meta.startTimestamp }, { it.id }))
    }

    /** Deletes an uploaded frame. */
    fun delete(frame: SealedFrame) {
        frame.metaFile.delete()
        frame.frameFile.delete()
    }

    /** Moves a rejected frame to `quarantine/` so it is never retried. */
    fun quarantine(frame: SealedFrame) {
        quarantineDir.mkdirs()
        frame.frameFile.renameTo(File(quarantineDir, frame.frameFile.name))
        frame.metaFile.renameTo(File(quarantineDir, frame.metaFile.name))
    }

    fun quarantined(): List<File> = quarantineDir.listFiles()?.sortedBy { it.name } ?: emptyList()

    /**
     * Seals the raw files left by earlier sessions (for example, after a process death). Each
     * file's records are sorted and split by window, so every recovered frame is valid. Files of
     * [activeSessionId], or of any session id that starts with `"$activeSessionId-"`, are left
     * alone.
     */
    fun recoverOpenFrames(activeSessionId: String?, compressor: FrameCompressor): List<SealedFrame> {
        val recovered = mutableListOf<SealedFrame>()
        for (file in openFiles()) {
            val parts = file.name.removeSuffix(OPEN_SUFFIX).split(SEPARATOR)
            if (parts.size != 3) continue
            val (sensorSpec, sessionId, _) = parts
            if (activeSessionId != null && (sessionId == activeSessionId || sessionId.startsWith("$activeSessionId-"))) continue
            val bytes = file.readBytes()
            val whole = bytes.size - bytes.size % AccelRecordPacker.RECORD_SIZE
            val records = AccelRecordPacker.unpack(bytes.copyOf(whole))
            records.groupBy { FrameWindow.indexOf(it.timestamp) }.toSortedMap().forEach { (window, inWindow) ->
                inWindow.chunked(FramePartitioner.MAX_RECORDS_PER_FRAME).forEachIndexed { i, chunk ->
                    val id = "${file.name.removeSuffix(OPEN_SUFFIX)}${SEPARATOR}r$window-$i"
                    recovered.add(sealRecords(id, sensorSpec, AccelRecordPacker.sortStable(chunk), compressor))
                }
            }
            file.delete()
        }
        return recovered
    }

    private fun sealRecords(id: String, sensorSpec: String, sorted: List<AccelRecord>, compressor: FrameCompressor): SealedFrame {
        sealedDir.mkdirs()
        val compressed = compressor.compress(AccelRecordPacker.packUnsorted(sorted))
        val meta = FrameMetadata.forFrame(sensorSpec, sorted, compressed, compressor)
        val frameFile = File(sealedDir, id + FRAME_SUFFIX)
        val metaFile = File(sealedDir, id + META_SUFFIX)
        writeAtomically(frameFile, compressed)
        writeAtomically(metaFile, metaJson(meta).toByteArray(Charsets.UTF_8))
        return SealedFrame(id, frameFile, metaFile, meta)
    }

    private fun metaJson(meta: FrameMetadata): String {
        val o = JsonObject()
        meta.formFields().forEach { (k, v) ->
            when (k) {
                "start_timestamp", "end_timestamp", "sample_count", "codec_format_version" -> o.addProperty(k, v.toLong())
                else -> o.addProperty(k, v)
            }
        }
        o.addProperty("byte_length", meta.byteLength)
        return gson.toJson(o)
    }

    private fun readMeta(file: File): FrameMetadata {
        val o = JsonParser().parse(file.readText(Charsets.UTF_8)).asJsonObject
        return FrameMetadata(
            sensorSpec = o["sensor_spec"].asString,
            startTimestamp = o["start_timestamp"].asLong,
            endTimestamp = o["end_timestamp"].asLong,
            sampleCount = o["sample_count"].asInt,
            codec = o["codec"].asString,
            codecFormatVersion = o["codec_format_version"].asInt,
            sha256 = o["sha256"].asString,
            byteLength = o["byte_length"].asInt,
            platform = o["platform"].asString,
            unit = o["unit"].asString
        )
    }

    private fun writeAtomically(target: File, bytes: ByteArray) {
        val tmp = File(target.parentFile, target.name + ".tmp")
        FileOutputStream(tmp).use {
            it.write(bytes)
            it.fd.sync()
        }
        if (!tmp.renameTo(target)) {
            tmp.delete()
            throw java.io.IOException("could not write $target")
        }
    }

    private fun frameId(sensorSpec: String, sessionId: String, key: FramePartitioner.FrameKey) =
        "$sensorSpec$SEPARATOR$sessionId$SEPARATOR${key.window}-${key.sequence}"

    private fun openFile(sensorSpec: String, sessionId: String, key: FramePartitioner.FrameKey) =
        File(openDir, frameId(sensorSpec, sessionId, key) + OPEN_SUFFIX)

    companion object {
        private const val SEPARATOR = "__"
        private const val OPEN_SUFFIX = ".raw"
        private const val FRAME_SUFFIX = ".frame"
        private const val META_SUFFIX = ".json"
    }
}
