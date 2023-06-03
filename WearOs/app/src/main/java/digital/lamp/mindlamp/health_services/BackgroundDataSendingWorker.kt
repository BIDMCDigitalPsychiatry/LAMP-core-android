package digital.lamp.mindlamp.health_services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ViewModelProviders
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import digital.lamp.mindlamp.app.App
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import digital.lamp.mindlamp.viewmodels.repositories.WebServiceRepository
import kotlinx.coroutines.runBlocking
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import java.io.File
import java.io.FileReader
import java.lang.reflect.Type

@HiltWorker
class BackgroundDataSendingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    private var webServiceRepository = WebServiceRepository()

    override fun doWork(): Result {
        Log.d("new watch", "data sending Worker running")
        DebugLogs.writeToFile(" BackgroundDataSendingWorker dowork")
        runBlocking {
            val list:ArrayList<SensorEventData>
            synchronized(SensorStore) {
                  list = SensorStore.getStoredSensorValues()
                SensorStore.clear()
            }
                if (list!=null && list.isNotEmpty() && NetworkUtils.isNetworkAvailable(applicationContext)) {
                    webServiceRepository.callUpdateSensordataWS(
                        AppState.session.username, list,
                        webServiceRepository.getWebServiceResponseLiveData()
                    )

                    Log.d("myTag", "send values to server")

                    DebugLogs.writeToFile(" BackgroundDataSendingWorker data sent")

                }
            }

        return Result.success()
    }
}