package digital.lamp.mindlamp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.SensorSpec
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Screen
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.sensor.constants.SensorConstants
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenStateWorkManager(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private lateinit var oGson: Gson
    private lateinit var oAnalyticsDao: AnalyticsDao
    override suspend fun doWork(): Result {
        try {
            val notification =
                LampNotificationManager.showNotification(
                    context,
                    context.getString(digital.lamp.mindlamp.R.string.active_data_collection)
                )
            setForeground(ForegroundInfo(1010, notification))
            oGson = Gson()
            oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
            val frequency = inputData.getDouble("frequency", 0.0)
            SensorSpec
            Lamp.startScreen(context)//Start Screen Sensor
            //Sensor Observer
            Screen.sensorObserver = object : Screen.LAMPSensorObserver {
                override fun onScreenLocked() {
                    val batteryPercentage: Float =
                        (NetworkUtils.getBatteryPercentage(context).toFloat() / 100)
                    val data = DimensionData(
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
                        SensorConstants.ScreenStateRepresentation.SCREEN_LOCKED.value,
                        batteryPercentage,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        SensorConstants.ScreenState.SCREEN_LOCKED.value,
                        null,
                        null,
                        null, null
                    )
                    val sensorEventData =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )

                    getScreenState(sensorEventData)
                }

                override fun onScreenOff() {
                    val batteryPercentage: Float =
                        (NetworkUtils.getBatteryPercentage(context).toFloat() / 100)
                    val data = DimensionData(
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
                        SensorConstants.ScreenStateRepresentation.SCREEN_OFF.value,
                        batteryPercentage,
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_OFF.value, null, null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )

                    getScreenState(sensorEventRequest)
                }

                override fun onScreenOn() {
                    val batteryPercentage: Float =
                        (NetworkUtils.getBatteryPercentage(context).toFloat() / 100)
                    val data = DimensionData(
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
                        SensorConstants.ScreenStateRepresentation.SCREEN_ON.value,
                        batteryPercentage,
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_ON.value, null, null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )

                    getScreenState(sensorEventRequest)
                }

                override fun onScreenUnlocked() {
                    val batteryPercentage: Float =
                        (NetworkUtils.getBatteryPercentage(context).toFloat() / 100)
                    val data = DimensionData(
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
                        SensorConstants.ScreenStateRepresentation.SCREEN_UNLOCKED.value,
                        batteryPercentage,
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_UNLOCKED.value, null, null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    getScreenState(sensorEventRequest)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Result.success()
    }
    private fun getScreenState(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }
}