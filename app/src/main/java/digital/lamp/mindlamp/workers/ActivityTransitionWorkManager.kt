package digital.lamp.mindlamp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.models.ActivityData
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.ActivityTransitions
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.dao.SensorDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityTransitionWorkManager(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private lateinit var oGson: Gson
    private lateinit var oAnalyticsDao: AnalyticsDao
    private lateinit var oSensorDao: SensorDao
    override suspend fun doWork(): Result {
        val notification =
            LampNotificationManager.showNotification(
                context,
                context.getString(digital.lamp.mindlamp.R.string.active_data_collection)
            )
        setForeground(ForegroundInfo(1010, notification))
        oGson = Gson()
        oSensorDao = AppDatabase.getInstance(context).sensorDao()
        oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()

        try {
            var sensorSpecList = arrayListOf<SensorSpecs>()
            CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                sensorSpecList = oSensorDao.getSensorsList() as ArrayList<SensorSpecs>
            }
            Lamp.startActivityTransition(context)//start sensor

            //Sensor Observer
            ActivityTransitions.setSensorObserver { activityType, transitionType ->
                var activityTransitionDataRequired = false
                if (sensorSpecList.isEmpty()) {
                    activityTransitionDataRequired = false
                } else {
                    sensorSpecList.forEach {
                        if (it.spec == Sensors.ACTIVITY_RECOGNITION.sensor_name) {
                            activityTransitionDataRequired = true
                        }
                    }
                }
                if (activityTransitionDataRequired) {
                    val activityData = ActivityData()
                    when (activityType) {
                        "running" -> {
                            activityData.running = transitionType
                        }
                        "cycling" -> {
                            activityData.cycling = transitionType
                        }
                        "in_car" -> {
                            activityData.automotive = transitionType
                        }
                        "stationary" -> {
                            activityData.stationary = transitionType
                        }
                        "unknown" -> {
                            activityData.unknown = transitionType
                        }
                        "walking" -> {
                            activityData.walking = transitionType
                        }
                        "on_foot" -> {
                            activityData.on_foot = transitionType
                        }
                    }
                    val dimensionData =
                        DimensionData(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null, null, null,
                            null, null, null, activityData, null, null
                        )
                    val sensorEventData =
                        SensorEvent(
                            dimensionData,
                            Sensors.ACTIVITY_RECOGNITION.sensor_name,
                            System.currentTimeMillis().toDouble()
                        )


                    getActivityData(sensorEventData)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Result.success()
    }
    private fun getActivityData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }

}