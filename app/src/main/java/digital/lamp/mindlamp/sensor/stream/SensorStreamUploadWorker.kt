package digital.lamp.mindlamp.sensor.stream

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.LampLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Uploads sealed accelerometer frames to `POST /participant/{id}/sensor-stream`, oldest first,
 * and applies [UploadPolicy] to each response. Frames are sent exactly as stored.
 */
class SensorStreamUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!SensorStreamUploads.isSignedIn()) return@withContext Result.success()
        if (!SensorStreamUploads.canUpload(applicationContext)) return@withContext Result.retry()
        // Upload only the signed-in participant's frames, to that participant.
        val participantId = AppState.session.userId
        val store = HighRateAccelerometerCollector.store(applicationContext, participantId)
        val authorization = SensorStreamUploads.authorizationHeader()
        for (frame in store.sealed()) {
            if (isStopped) return@withContext Result.retry()
            val code = try {
                val request = MultipartFrameRequest.build(
                    AppState.session.serverAddress, participantId, authorization,
                    frame.meta, frame.bytes()
                )
                SensorStreamUploads.httpClient.newCall(request).execute().use { it.code }
            } catch (e: IOException) {
                LampLog.e(TAG, "sensor-stream upload failed: ${e.message}")
                null
            }
            when (UploadPolicy.actionFor(code)) {
                UploadAction.DELETE -> store.delete(frame)
                UploadAction.QUARANTINE -> {
                    LampLog.e(TAG, "sensor-stream frame ${frame.id} rejected with HTTP $code")
                    store.quarantine(frame)
                }
                UploadAction.RETRY_LATER -> return@withContext Result.retry()
            }
        }
        Result.success()
    }

    companion object {
        private const val TAG = "SensorStreamUpload"
        const val WORK_NAME = "sensor_stream_upload"

        /** Schedules an upload when the network is connected. Repeated calls coalesce. */
        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<SensorStreamUploadWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }
    }
}
