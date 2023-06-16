package digital.lamp.mindlamp.sensor.health_services

import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
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
            dataPoints.getData(DataType.LOCATION).forEach {
                //  Log.d("Watch", "Location: ${it.value.latitude } , ${it.value.longitude }, ${it.value.bearing }, , ${it.value.altitude }")


            }
            dataPoints.getData(DataType.CALORIES).forEach {
                //   Log.d("Watch", "Calories value: ${it.value}")


            }
            dataPoints.getData(DataType.VO2_MAX).forEach {
                // Log.d("Watch", "Vo2max value: ${it.value}")


            }
        }
    }

}
