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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Posts queued `lamp.gps1m` events to `POST /participant/{id}/sensor_event` in batches.
 */
class Gps1mUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!SensorStreamUploads.isSignedIn()) return@withContext Result.success()
        if (!SensorStreamUploads.canUpload(applicationContext)) return@withContext Result.retry()
        val queue = Gps1mCollector.queue(applicationContext)
        val url = MultipartFrameRequest.participantUrl(AppState.session.serverAddress, AppState.session.userId) +
            "/sensor_event"
        val authorization = SensorStreamUploads.authorizationHeader()
        while (!isStopped) {
            val batch = queue.next(BATCH_SIZE)
            if (batch.events.isEmpty()) return@withContext Result.success()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", authorization)
                .post(Gps1mEvents.toJsonArray(batch.events).toRequestBody(JSON))
                .build()
            val code = try {
                SensorStreamUploads.httpClient.newCall(request).execute().use { it.code }
            } catch (e: IOException) {
                LampLog.e(TAG, "lamp.gps1m upload failed: ${e.message}")
                null
            }
            when (UploadPolicy.actionFor(code)) {
                UploadAction.DELETE -> queue.remove(batch)
                UploadAction.QUARANTINE -> {
                    // The server rejected the batch as invalid; resending can't succeed.
                    LampLog.e(TAG, "lamp.gps1m batch rejected with HTTP $code; dropping ${batch.events.size} events")
                    queue.remove(batch)
                }
                UploadAction.RETRY_LATER -> return@withContext Result.retry()
            }
        }
        Result.retry()
    }

    companion object {
        private const val TAG = "Gps1mUpload"
        private const val BATCH_SIZE = 100
        private val JSON = "application/json; charset=utf-8".toMediaType()
        const val WORK_NAME = "gps1m_upload"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<Gps1mUploadWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }
    }
}
