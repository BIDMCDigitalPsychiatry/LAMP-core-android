package digital.lamp.mindlamp.sensor.health_services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.viewmodels.repositories.WebServiceRepository
import kotlinx.coroutines.runBlocking
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData

@HiltWorker
class BackgroundDataSendingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    private var webServiceRepository = WebServiceRepository()

    override fun doWork(): Result {
        if (!AppState.session.isLoggedIn) {
            return Result.success()
        }
        Log.d("new watch", "data sending Worker running")
        DebugLogs.writeToFile(" BackgroundDataSendingWorker dowork")
        runBlocking {
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

                Log.d("myTag", "send values to server")

                // DebugLogs.writeToFile(" BackgroundDataSendingWorker data sent")

            }
        }

        return Result.success()
    }
}