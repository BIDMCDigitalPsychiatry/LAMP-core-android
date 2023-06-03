package digital.lamp.mindlamp.health_services

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class HealthServiceDataService : PassiveListenerService() {
    @Inject
    lateinit var repository: HealthServiceDataRepository

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        runBlocking {
            dataPoints.getData(DataType.HEART_RATE_BPM).latestHeartRate()?.let {
                repository.storeLatestHeartRate(it)

            }
            dataPoints.getData(DataType.LOCATION).forEach { location ->
                Log.d("Watch", "Location: $location")


            }
            dataPoints.getData(DataType.CALORIES).forEach {
                Log.d("Watch", "Calories value: $it")


            }
        }
    }

}
