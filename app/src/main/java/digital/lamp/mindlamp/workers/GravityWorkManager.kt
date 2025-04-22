package digital.lamp.mindlamp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.models.AttitudeData
import digital.lamp.lamp_kotlin.lamp_core.models.DeviceMotionData
import digital.lamp.lamp_kotlin.lamp_core.models.GravityData
import digital.lamp.lamp_kotlin.lamp_core.models.MagnetData
import digital.lamp.lamp_kotlin.lamp_core.models.MotionData
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Gravity
import digital.lamp.lamp_kotlin.sensor_core.Gyroscope
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GravityWorkManager(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private lateinit var oGson: Gson
    private lateinit var oAnalyticsDao: AnalyticsDao

    override suspend fun doWork(): Result {
        val notification =
            LampNotificationManager.showNotification(
                context,
                context.getString(digital.lamp.mindlamp.R.string.active_data_collection)
            )
        setForeground(ForegroundInfo(1010, notification))
        oGson = Gson()
        oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
        val frequency = inputData.getDouble("frequency", 0.0)
        try {
            Lamp.startGravity(context)//Start Gyroscope Sensor
            frequency?.let {
                val interval = (1 / frequency!!) * 1000
                Gravity.setInterval(interval.toLong())// 1 millisecond
            }
            //Sensor Observer
            Gravity.setSensorObserver {
                val x = it.getAsDouble(Gyroscope.VALUES_0)
                val y = it.getAsDouble(Gyroscope.VALUES_1)
                val z = it.getAsDouble(Gyroscope.VALUES_2)

                val gravityData = GravityData(x, y, z)
                val dimensionData = DeviceMotionData(
                    MotionData(null, null, null), MagnetData(null, null, null),
                    AttitudeData(null, null, null), gravityData, RotationData(null, null, null)
                )
                val sensorEventData =
                    SensorEvent(
                        dimensionData,
                        Sensors.DEVICE_MOTION.sensor_name, System.currentTimeMillis().toDouble()
                    )
                getGyroscopeData(sensorEventData)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Result.success()
    }
    private  fun getGyroscopeData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }
}