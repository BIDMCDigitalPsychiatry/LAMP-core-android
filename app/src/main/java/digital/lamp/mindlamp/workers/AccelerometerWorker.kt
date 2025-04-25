
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.models.AttitudeData
import digital.lamp.lamp_kotlin.lamp_core.models.DeviceMotionData
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.GravityData
import digital.lamp.lamp_kotlin.lamp_core.models.MagnetData
import digital.lamp.lamp_kotlin.lamp_core.models.MotionData
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.util.AccelerometerDataCollectWorkerManager
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccelerometerDataWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private lateinit var oGson: Gson
    private lateinit var oAnalyticsDao: AnalyticsDao
    override suspend fun doWork(): Result {
        Log.d("AccelerometerDataWorker", "Worker started ✅")
        oGson = Gson()
        oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
        val sensorSpec = inputData.getString("spec")
        val frequency = inputData.getDouble("frequency", 0.0)
        Log.d("AccelerometerDataWorker", "Spec: $sensorSpec | Frequency: $frequency")
        try {
            // Accelerometer settings
            var interval = 0.0
            if (frequency > 0) {
                interval = (1 / frequency) * 1000
                AccelerometerDataCollectWorkerManager.setInterval(interval.toLong())
            }
            val data = workDataOf(
                "interval" to interval,
            )
            val accelWorkerRequest = OneTimeWorkRequestBuilder<AccelerometerDataCollectWorkerManager>()
                .addTag("ACCELEROMETER")
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(accelWorkerRequest)
            Log.d("AccelerometerDataWorker", "Accelerometer started ✅")
            // Sensor Observer
            AccelerometerDataCollectWorkerManager.setSensorObserver {
                val x = it.getAsDouble(Accelerometer.VALUES_0)
                val y = it.getAsDouble(Accelerometer.VALUES_1)
                val z = it.getAsDouble(Accelerometer.VALUES_2)

                val motionData = MotionData(x, y, z)

                when (sensorSpec) {
                    Sensors.DEVICE_MOTION.sensor_name -> {
                        val deviceMotionData = DeviceMotionData(
                            motionData,
                            MagnetData(null, null, null),
                            AttitudeData(null, null, null),
                            GravityData(null, null, null),
                            RotationData(null, null, null)
                        )
                        val sensorEventData = SensorEvent(
                            deviceMotionData,
                            Sensors.DEVICE_MOTION.sensor_name,
                            System.currentTimeMillis().toDouble()
                        )

                        // Send data back to listener
                        sendDataToListener(sensorEventData)
                    }

                    Sensors.ACCELEROMETER.sensor_name -> {
                        val dimensionData = DimensionData(
                            x, y, z,
                            null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null,
                            null, null, null, null, null
                        )
                        val sensorEventDataAccelerometer = SensorEvent(
                            dimensionData,
                            Sensors.ACCELEROMETER.sensor_name,
                            System.currentTimeMillis().toDouble()
                        )

                        // Send data back to listener
                        sendDataToListener(sensorEventDataAccelerometer)
                    }

                    else -> {
                        val deviceMotionData = DeviceMotionData(
                            motionData,
                            MagnetData(null, null, null),
                            AttitudeData(null, null, null),
                            GravityData(null, null, null),
                            RotationData(null, null, null)
                        )
                        val sensorEventData = SensorEvent(
                            deviceMotionData,
                            Sensors.DEVICE_MOTION.sensor_name,
                            System.currentTimeMillis().toDouble()
                        )
                        sendDataToListener(sensorEventData)

                        val dimensionData = DimensionData(
                            x, y, z,
                            null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null,
                            null, null, null, null, null
                        )
                        val sensorEventDataAccelerometer = SensorEvent(
                            dimensionData,
                            Sensors.ACCELEROMETER.sensor_name,
                            System.currentTimeMillis().toDouble()
                        )
                        sendDataToListener(sensorEventDataAccelerometer)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    private fun sendDataToListener(sensorEventData: SensorEvent) {
        // Pass the sensorEventData back to the caller (e.g., via a callback or another mechanism)
        // You can use WorkManager's outputData or any other mechanism for this purpose
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch{
            val id = oAnalyticsDao.insertAnalytics(oAnalytics)
            Log.e("data inserted","${sensorEventData.data}")
        }
    }
}