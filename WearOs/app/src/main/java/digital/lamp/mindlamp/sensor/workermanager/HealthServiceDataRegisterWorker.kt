package digital.lamp.mindlamp.sensor.workermanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import digital.lamp.mindlamp.sensor.health_services.HealthServicesManager
import kotlinx.coroutines.runBlocking

@HiltWorker
public class HealthServiceDataRegisterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthServicesManager: HealthServicesManager
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.i("LampWatch", "Worker running")

        runBlocking {
            healthServicesManager.registerHealthServicesData()
        }
        return Result.success()
    }
}