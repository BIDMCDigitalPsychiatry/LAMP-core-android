package digital.lamp.mindlamp.sensor.workermanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import digital.lamp.mindlamp.app.App
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.sensor.health_services.SensorStore
import digital.lamp.mindlamp.service.LampForegroundService
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.viewmodels.repositories.WebServiceRepository
import kotlinx.coroutines.runBlocking
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import java.util.concurrent.TimeUnit

@HiltWorker
class BackgroundDataSendingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    private var webServiceRepository = WebServiceRepository()

    companion object {
        private val TAG = LampForegroundService::class.java.simpleName
        private const val TIME_INTERVAL: Long = 60000
        private var lastSentTimeMillis: Long = 0

    }

    override   fun doWork(): Result {
        DebugLogs.writeToFile(" BackgroundDataSendingWorker doWork()")
        if (!AppState.session.isLoggedIn) {
            return Result.success()
        }

        if (lastSentTimeMillis > 0 && System.currentTimeMillis() - lastSentTimeMillis < TIME_INTERVAL) {

            DebugLogs.writeToFile("too frequent webservice call: call cancelled")
            return Result.success()
        }

        lastSentTimeMillis = System.currentTimeMillis()
        Log.d("new watch", "data sending Worker running")
        DebugLogs.writeToFile(" BackgroundDataSendingWorker sending data")

        val list: ArrayList<SensorEventData>
        synchronized(SensorStore) {
            list = SensorStore.getStoredSensorValues()
            SensorStore.clear()
        }
        if (list != null && list.isNotEmpty() && NetworkUtils.isNetworkAvailable(
                applicationContext
            )
        ) {
            webServiceRepository.callUpdateSensordataWS(
                AppState.session.username, list,
                webServiceRepository.getWebServiceResponseLiveData()
            )

            DebugLogs.writeToFile(" BackgroundDataSendingWorker data sent : List size" + list.size)


            // DebugLogs.writeToFile(" BackgroundDataSendingWorker data sent")

        }

        val sendDataRequest =
            OneTimeWorkRequestBuilder<BackgroundDataSendingWorker> ()
                .setInitialDelay(5, TimeUnit.MINUTES)
                // Additional configuration
                //   .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        WorkManager.getInstance(App.app)
            .enqueue(
                sendDataRequest
            )


        return Result.success()
    }
}