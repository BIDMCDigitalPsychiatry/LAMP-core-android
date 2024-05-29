package digital.lamp.mindlamp.sensor.health_services

import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import dagger.hilt.android.AndroidEntryPoint
import digital.lamp.mindlamp.utils.LampLog
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class HealthServiceDataService : PassiveListenerService() {
    @Inject
    lateinit var repository: HealthServiceDataRepository

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
       // runBlocking {
            try {
                dataPoints.getData(DataType.HEART_RATE_BPM).latestHeartRate()?.let {
                    repository.storeLatestHeartRate(it)

                }
                dataPoints.getData(DataType.STEPS).forEach {
                    //  Log.d("Watch", "Location: ${it.value.latitude } , ${it.value.longitude }, ${it.value.bearing }, , ${it.value.altitude }")
                    //  (" it.startDurationFromBoot
                    var bootTimestamp =
                        java.lang.System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime()
                    var stepsStartTimestamp = bootTimestamp + it.startDurationFromBoot.toMillis()
                    var stepsEndTimestamp = bootTimestamp + it.endDurationFromBoot.toMillis()
                    repository.storeStepsData(stepsEndTimestamp, it.value as Integer)
                    digital.lamp.mindlamp.utils.DebugLogs.writeToFile(" Steps received ::start= " + stepsStartTimestamp + ":: End=" + stepsEndTimestamp + ":value=" + it.value)

                }
                dataPoints.getData(DataType.CALORIES).forEach {
                    //   Log.d("Watch", "Calories value: ${it.value}")
                }
                dataPoints.getData(DataType.VO2_MAX).forEach {
                    // Log.d("Watch", "Vo2max value: ${it.value}")
                }
            } catch (e: Exception) {
                LampLog.e("Lamp", "HealthServiceDataService:onNewDataPointsReceived" + e.message, e)
            }
        }
  //  }

}
