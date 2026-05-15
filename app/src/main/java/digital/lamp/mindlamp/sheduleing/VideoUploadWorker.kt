package digital.lamp.mindlamp.sheduleing

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.apis.VideoDiaryAPI
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadByteRange
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadCompleteRequest
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadCompletedPart
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadInitiateRequest
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadInitiateResponse
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadMetadata
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadPart
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadRefreshUrlsRequest
import digital.lamp.lamp_kotlin.lamp_core.models.VideoUploadRefreshUrlsResponse
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.RandomAccessFile
import java.util.Locale

class VideoUploadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "VideoUploadWorker"

        const val TAG_VIDEO_UPLOAD = "video_diary_upload"

        // Cap retries so the user eventually sees a real "failed" state instead of
        // an indefinitely pending upload. WorkManager itself uses runAttemptCount.
        const val MAX_RUN_ATTEMPTS = 5

        const val KEY_FILE_PATH = "file_path"
        const val KEY_PARTICIPANT_ID = "participant_id"
        const val KEY_ACTIVITY_ID = "activity_id"
        const val KEY_RESOLUTION = "resolution"
        const val KEY_FRAME_RATE = "frame_rate"
        const val KEY_MAX_BITRATE_MBPS = "max_bitrate_mbps"
        const val KEY_ELAPSED_SECONDS = "elapsed_seconds"

        const val KEY_OUTPUT_UPLOAD_ID = "out_upload_id"
        const val KEY_OUTPUT_PARTICIPANT_ID = "out_participant_id"
        const val KEY_OUTPUT_ACTIVITY_ID = "out_activity_id"
        const val KEY_OUTPUT_FILE_PATH = "out_file_path"
        const val KEY_OUTPUT_VIDEO_KEY = "out_video_key"
        const val KEY_OUTPUT_DURATION = "out_duration"
        const val KEY_OUTPUT_RESOLUTION = "out_resolution"
        const val KEY_OUTPUT_FILE_SIZE = "out_file_size"
        const val KEY_OUTPUT_MIME_TYPE = "out_mime_type"
        const val KEY_OUTPUT_VIDEO_TIMESTAMP = "out_video_timestamp"
        const val KEY_OUTPUT_EVENT_TIMESTAMP = "out_event_timestamp"
        const val KEY_OUTPUT_ERROR = "out_error"

        // Live progress (read via WorkInfo.progress)
        const val KEY_PROGRESS_PERCENT = "progress_percent"
        const val KEY_PROGRESS_STATUS = "progress_status"
        const val STATUS_STARTING = "starting"
        const val STATUS_UPLOADING = "uploading"
        const val STATUS_FINALIZING = "finalizing"
        const val STATUS_WAITING_NETWORK = "waiting_network"

        // Notification setup
        const val NOTIFICATION_CHANNEL_ID = "video_upload_status"
        private const val NOTIFICATION_ID = 4001
    }

    private val gson = Gson()
    private val uploadHttpClient = OkHttpClient()
    private val participantId: String
        get() = inputData.getString(KEY_PARTICIPANT_ID).orEmpty()

    override suspend fun doWork(): Result {
        val filePath = inputData.getString(KEY_FILE_PATH).orEmpty()
        val activityId = inputData.getString(KEY_ACTIVITY_ID).orEmpty()
        val file = File(filePath)
        return try {
            if (!file.exists()) {
                Log.e(TAG, "Video file does not exist: ${file.absolutePath}")
               /* showFailureNotification()*/
                return Result.failure(failureData(activityId, filePath, "Video file does not exist"))
            }
            if (participantId.isEmpty()) {
                Log.e(TAG, "Missing participant id")
                /*showFailureNotification()*/
                return Result.failure(failureData(activityId, filePath, "Missing participant id"))
            }

         //   updateStatus(STATUS_STARTING, 0, context.getString(R.string.video_upload_starting))
            ensureNetworkAvailable()
            val result = uploadVideo(file)
            Log.d(TAG, "Video upload complete response: ${result.completeResponse}")

           /* cancelNotification()*/
            Result.success(buildSuccessOutput(file, activityId, result))
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Video upload failed", e)
            if (e.message == "No internet connection" && runAttemptCount < MAX_RUN_ATTEMPTS - 1) {
               /* updateStatus(
                    STATUS_WAITING_NETWORK,
                    -1,
                    context.getString(R.string.video_upload_waiting_network)
                )*/
                Result.retry()
            } else {
                /*showFailureNotification()*/
                Result.failure(failureData(activityId, filePath, e.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Video upload failed", e)
            // Network/IO blip → retry with backoff up to MAX_RUN_ATTEMPTS,
            // then surface a permanent failure to the UI/notification layer.
            if (runAttemptCount < MAX_RUN_ATTEMPTS - 1) {
               /* updateStatus(
                    STATUS_WAITING_NETWORK,
                    -1,
                    context.getString(R.string.video_upload_slow)
                )*/
                Result.retry()
            } else {
               /* showFailureNotification()*/
                Result.failure(failureData(activityId, filePath, e.message))
            }
        }
    }

    private data class UploadOutcome(
        val uploadId: String,
        val completeResponse: String,
        val metadata: VideoUploadMetadata
    )

    private fun failureData(activityId: String, filePath: String, message: String?): Data =
        workDataOf(
            KEY_OUTPUT_PARTICIPANT_ID to participantId,
            KEY_OUTPUT_ACTIVITY_ID to activityId,
            KEY_OUTPUT_FILE_PATH to filePath,
            KEY_OUTPUT_ERROR to (message ?: "unknown_error"),
            // Echo input data back so a "Retry" tap on the banner/notification
            // can re-enqueue with the original parameters.
            KEY_RESOLUTION to inputData.getInt(KEY_RESOLUTION, 0),
            KEY_FRAME_RATE to inputData.getInt(KEY_FRAME_RATE, 0),
            KEY_MAX_BITRATE_MBPS to inputData.getInt(KEY_MAX_BITRATE_MBPS, 0),
            KEY_ELAPSED_SECONDS to inputData.getInt(KEY_ELAPSED_SECONDS, 0)
        )

    private fun buildSuccessOutput(
        file: File,
        activityId: String,
        outcome: UploadOutcome
    ): Data {
        val metadata = outcome.metadata
        val durationSec = metadata.duration ?: 0.0
        val resolution = metadata.height
            ?.takeIf { it > 0 }
            ?.let { "${it}p" }
            .orEmpty()
        val mimeType = when (metadata.codec) {
            "h264" -> "video/mp4;codecs=avc1"
            "h265" -> "video/mp4;codecs=hevc"
            null, "" -> "video/mp4"
            else -> "video/mp4;codecs=${metadata.codec}"
        }
        val recordedAtMs = file.lastModified().takeIf { it > 0 } ?: System.currentTimeMillis()
        return workDataOf(
            KEY_OUTPUT_UPLOAD_ID to outcome.uploadId,
            KEY_OUTPUT_PARTICIPANT_ID to participantId,
            KEY_OUTPUT_ACTIVITY_ID to activityId,
            KEY_OUTPUT_FILE_PATH to file.absolutePath,
            KEY_OUTPUT_VIDEO_KEY to outcome.uploadId,
            KEY_OUTPUT_DURATION to String.format(Locale.US, "%.2f", durationSec),
            KEY_OUTPUT_RESOLUTION to resolution,
            KEY_OUTPUT_FILE_SIZE to file.length().toString(),
            KEY_OUTPUT_MIME_TYPE to mimeType,
            KEY_OUTPUT_VIDEO_TIMESTAMP to recordedAtMs.toString(),
            KEY_OUTPUT_EVENT_TIMESTAMP to System.currentTimeMillis()
        )
    }

    private suspend fun uploadVideo(file: File): UploadOutcome {
        val api = VideoDiaryAPI(AppConstants.VIDEO_DIARY_UPLOAD_URL)
        ensureNetworkAvailable()
        val metadata = buildVideoUploadMetadata(file)
        val initiateResponse = api.videoUploadInitiate(
            participantId = participantId,
            videoUploadInitiateRequest = VideoUploadInitiateRequest(
                participantId = participantId,
                metadata = metadata
            ),
            token = AppConstants.VIDEO_DIARY_UPLOAD_TOKEN
        )
        Log.d(TAG, "Video upload initiate response: $initiateResponse")

        val upload = gson.fromJson(initiateResponse, VideoUploadInitiateResponse::class.java)
        val uploadId = upload.id ?: error("Missing upload id")
        val uploadParts = upload.parts.orEmpty()
        if (uploadParts.isEmpty()) {
            error("No upload parts returned")
        }

        val totalParts = uploadParts.size
        val completedParts = uploadParts.mapIndexed { index, part ->
            val partNumber = part.partNumber ?: error("Missing part number")
            val byteRange = part.byteRange ?: error("Missing byte range for part $partNumber")
            val start = byteRange.start ?: error("Missing byte range start for part $partNumber")
            val end = byteRange.end ?: error("Missing byte range end for part $partNumber")
            val url = part.presignedUrl ?: error("Missing presigned URL for part $partNumber")
            val method = part.method ?: "PUT"

            // Pre-part progress: report the % about to be attempted so the UI moves
            // even on slow networks where individual parts take a while.
            val percentBefore = (index * 100) / totalParts
           /* updateStatus(
                STATUS_UPLOADING,
                percentBefore,
                context.getString(R.string.video_upload_uploading, percentBefore)
            )*/

            val etag = uploadPartWithRefresh(
                api = api,
                file = file,
                uploadId = uploadId,
                part = part,
                partNumber = partNumber,
                url = url,
                method = method,
                start = start,
                end = end
            )

            // Post-part progress
            val percentAfter = ((index + 1) * 100) / totalParts
           /* updateStatus(
                STATUS_UPLOADING,
                percentAfter,
                context.getString(R.string.video_upload_uploading, percentAfter)
            )*/

            VideoUploadCompletedPart(partNumber = partNumber, etag = etag)
        }

        val completeRequest = VideoUploadCompleteRequest(
            id = uploadId,
            parts = completedParts
        )
        Log.d(TAG, "Video upload complete request: ${gson.toJson(completeRequest)}")

        ensureNetworkAvailable()
     /*   updateStatus(
            STATUS_FINALIZING,
            99,
            context.getString(R.string.video_upload_finalizing)
        )*/
        val completeResponse = api.videoUploadComplete(
            participantId = participantId,
            videoUploadCompleteRequest = completeRequest,
            token = AppConstants.VIDEO_DIARY_UPLOAD_TOKEN
        )
        return UploadOutcome(
            uploadId = uploadId,
            completeResponse = completeResponse,
            metadata = metadata
        )
    }

    private fun uploadPartWithRefresh(
        api: VideoDiaryAPI,
        file: File,
        uploadId: String,
        part: VideoUploadPart,
        partNumber: Int,
        url: String,
        method: String,
        start: Long,
        end: Long
    ): String {
        return try {
            uploadPart(file, url, method, start, end, partNumber)
        } catch (firstError: Exception) {
            Log.w(TAG, "Refreshing presigned URL for part $partNumber after upload failure", firstError)
            ensureNetworkAvailable()
            val refreshedPart = refreshUploadPartUrl(api, uploadId, partNumber)
            val refreshedByteRange = refreshedPart.byteRange ?: part.byteRange
                ?: error("Missing refreshed byte range for part $partNumber")
            val refreshedStart = refreshedByteRange.start ?: start
            val refreshedEnd = refreshedByteRange.end ?: end
            val refreshedUrl = refreshedPart.presignedUrl
                ?: error("Missing refreshed presigned URL for part $partNumber")
            val refreshedMethod = refreshedPart.method ?: method
            uploadPart(file, refreshedUrl, refreshedMethod, refreshedStart, refreshedEnd, partNumber)
        }
    }

    private fun refreshUploadPartUrl(
        api: VideoDiaryAPI,
        uploadId: String,
        partNumber: Int
    ): VideoUploadPart {
        val refreshResponse = api.videoUploadRefreshUrls(
            participantId = participantId,
            videoUploadRefreshUrlsRequest = VideoUploadRefreshUrlsRequest(
                id = uploadId,
                partNumbers = listOf(partNumber)
            ),
            token = AppConstants.VIDEO_DIARY_UPLOAD_TOKEN
        )
        Log.d(TAG, "Video upload refresh URL response: $refreshResponse")
        val refreshed = gson.fromJson(refreshResponse, VideoUploadRefreshUrlsResponse::class.java)
        val refreshedPart = refreshed.parts.orEmpty()
            .firstOrNull { it.partNumber == partNumber }
            ?: error("Refresh URL response missing part $partNumber")

        return VideoUploadPart(
            partNumber = refreshedPart.partNumber,
            byteRange = VideoUploadByteRange(
                start = refreshedPart.startByte,
                end = refreshedPart.endByte
            ),
            method = "PUT",
            presignedUrl = refreshedPart.presignedUrl,
            presignedUrlExpiration = refreshed.expiresAt
        )
    }

    private fun uploadPart(
        file: File,
        presignedUrl: String,
        method: String,
        start: Long,
        inclusiveEnd: Long,
        partNumber: Int
    ): String {
        ensureNetworkAvailable()
        val chunk = readByteRange(file, start, inclusiveEnd)
        val request = Request.Builder()
            .url(presignedUrl)
            .method(
                method,
                chunk.toRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()

        uploadHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Part upload failed: ${response.code} ${response.message}")
            }
            Log.d(TAG, "S3 part $partNumber status=${response.code}")
            Log.d(TAG, "S3 part $partNumber etag=${response.header("ETag")}")
            val etag = response.header("ETag") ?: error("Missing ETag from part upload")
            return normalizeEtag(etag)
        }
    }

    private fun normalizeEtag(etag: String): String {
        val trimmedEtag = etag.trim()
        return if (trimmedEtag.startsWith("\"") && trimmedEtag.endsWith("\"")) {
            trimmedEtag
        } else {
            "\"$trimmedEtag\""
        }
    }

    private fun readByteRange(file: File, start: Long, inclusiveEnd: Long): ByteArray {
        val length = inclusiveEnd - start + 1
        if (length <= 0) {
            throw IllegalArgumentException("Invalid byte range: $start-$inclusiveEnd")
        }
        if (length > Int.MAX_VALUE) {
            throw IllegalArgumentException("Upload part is too large: $length bytes")
        }

        return RandomAccessFile(file, "r").use { randomAccessFile ->
            val bytes = ByteArray(length.toInt())
            randomAccessFile.seek(start)
            randomAccessFile.readFully(bytes)
            bytes
        }
    }

    private fun buildVideoUploadMetadata(file: File): VideoUploadMetadata {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toDoubleOrNull()
            val capturedFrameRate =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                    ?.toDoubleOrNull()
                    ?.toInt()
            val capturedBitrate =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                    ?.toLongOrNull()
            val capturedHeight =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    ?.toIntOrNull()
            val capturedWidth =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    ?.toIntOrNull()

            VideoUploadMetadata(
                size = file.length(),
                codec = "h264",
                bitrate = capturedBitrate ?: inputData.getInt(KEY_MAX_BITRATE_MBPS, 0)
                    .takeIf { it > 0 }
                    ?.let { it * 1_000_000L },
                duration = durationMs?.div(1000.0) ?: inputData.getInt(KEY_ELAPSED_SECONDS, 0)
                    .toDouble(),
                frameRate = capturedFrameRate ?: inputData.getInt(KEY_FRAME_RATE, 0)
                    .takeIf { it > 0 },
                height = capturedHeight ?: inputData.getInt(KEY_RESOLUTION, 0)
                    .takeIf { it > 0 },
                width = capturedWidth
            )
        } finally {
            retriever.release()
        }
    }

    private fun extractVideoCodec(file: File): String? {
        val extractor = MediaExtractor()
        return try {
            extractor.setDataSource(file.absolutePath)
            for (index in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(index)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("video/") == true) {
                    return when (mime) {
                        "video/avc" -> "h264"
                        "video/hevc" -> "h265"
                        else -> mime.removePrefix("video/")
                    }
                }
            }
            null
        } finally {
            extractor.release()
        }
    }

    private fun ensureNetworkAvailable() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            throw IllegalStateException("No internet connection")
        }
    }

    /**
     * Pushes both:
     *  - WorkInfo.progress (consumed by HomeActivity's in-app banner)
     *  - An ongoing notification (visible even when the app is backgrounded)
     *
     *  @param percent  0..100, or -1 for indeterminate (e.g. waiting for network).
     */
   /* private suspend fun updateStatus(status: String, percent: Int, displayText: String) {
        try {
            setProgress(
                workDataOf(
                    KEY_PROGRESS_STATUS to status,
                    KEY_PROGRESS_PERCENT to percent
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to publish progress", e)
        }
        *//*showOngoingNotification(displayText, percent)*//*
    }*/

    /*private fun showOngoingNotification(text: String, percent: Int) {
        if (!notificationsAllowed()) return
        ensureNotificationChannel()

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setContentTitle(context.getString(R.string.video_upload_channel_name))
            .setContentText(text)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(homePendingIntent())

        if (percent in 0..100) {
            builder.setProgress(100, percent, false)
        } else {
            builder.setProgress(0, 0, true)
        }

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun showFailureNotification() {
        if (!notificationsAllowed()) return
        ensureNotificationChannel()

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setContentTitle(context.getString(R.string.video_upload_channel_name))
            .setContentText(context.getString(R.string.video_upload_failed))
            .setAutoCancel(true)
            .setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(homePendingIntent())

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }*/

   /* private fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }*/

   /* private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.video_upload_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.video_upload_channel_desc)
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }
        manager.createNotificationChannel(channel)
    }*/

   /* private fun homePendingIntent(): PendingIntent {
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }*/

   /* private fun notificationsAllowed(): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()*/
}
