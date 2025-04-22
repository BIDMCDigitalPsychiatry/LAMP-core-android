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
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Magnetometer
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MagnetoMeterWorkManager(private val context: Context, parameters: WorkerParameters) :
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
            Lamp.startMagnetometer(context)//start Sensor
            frequency.let {
                val interval = (1 / frequency) * 1000
                Magnetometer.setInterval(interval.toLong())// 1 millisecond
            }
            //Sensor Observer
            Magnetometer.setSensorObserver {
                val x = it.getAsDouble(Magnetometer.VALUES_0)
                val y = it.getAsDouble(Magnetometer.VALUES_1)
                val z = it.getAsDouble(Magnetometer.VALUES_2)
                //val value=it.
                val magnetData =
                    MagnetData(x, y, z)
                val data = DeviceMotionData(
                    MotionData(null, null, null),
                    magnetData,
                    AttitudeData(null, null, null),
                    GravityData(null, null, null),
                    RotationData(null, null, null)
                )
                val sensorEventData =
                    SensorEvent(
                        data,
                        Sensors.DEVICE_MOTION.sensor_name, System.currentTimeMillis().toDouble()
                    )
                getMagneticData(sensorEventData)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Result.success()
    }
    private fun getMagneticData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }
}
