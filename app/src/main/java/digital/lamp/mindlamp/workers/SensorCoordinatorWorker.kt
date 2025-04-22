package digital.lamp.mindlamp.workers

import AccelerometerDataWorker
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SensorCoordinatorWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val sensorDao = AppDatabase.getInstance(context).sensorDao()
        val sensorList = sensorDao.getSensorsList()

        sensorList.forEach { sensor ->
            when (sensor.spec) {
                Sensors.ACCELEROMETER.sensor_name, Sensors.DEVICE_MOTION.sensor_name -> {
                    val data = workDataOf(
                        "frequency" to (sensor.frequency ?: 0.0),
                        "spec" to (sensor.spec ?: "")
                    )
                    val request = OneTimeWorkRequestBuilder<AccelerometerDataWorker>()
                        .setInputData(data)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)
                }

                Sensors.DEVICE_MOTION.sensor_name -> {
                    val data = workDataOf(
                        "frequency" to (sensor.frequency ?: 0.0),
                        "spec" to (sensor.spec ?: "")
                    )
                   /* val request = OneTimeWorkRequestBuilder<RotationWorkerManager>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.DEVICE_MOTION.sensor_name -> {
                    val data = workDataOf(
                        "frequency" to (sensor.frequency ?: 0.0),
                        "spec" to (sensor.spec ?: "")
                    )
                 /*   val request = OneTimeWorkRequestBuilder<MagnetoMeterWorkManager>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.DEVICE_MOTION.sensor_name -> {
                    val data = workDataOf(
                        "frequency" to (sensor.frequency ?: 0.0),
                        "spec" to (sensor.spec ?: "")
                    )
                   /* val request = OneTimeWorkRequestBuilder<GravityWorkManager>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.GPS.sensor_name -> {
                    val data = workDataOf("frequency" to (sensor.frequency ?: 0.0))
                   /* val request = OneTimeWorkRequestBuilder<LocationWorkManager>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.SCREEN_STATE.sensor_name, Sensors.DEVICE_STATE.sensor_name -> {
                  /*  val request = OneTimeWorkRequestBuilder<ScreenStateWorkManager>().setExpedited(
                        OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
                    ).build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.TELEPHONY.sensor_name -> {
                   /* val request = OneTimeWorkRequestBuilder<TelephonyWorkManager>().setExpedited(
                        OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
                    ).build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.NEARBY_DEVICES.sensor_name -> {
                    val data = workDataOf("frequency" to (sensor.frequency ?: 0.0))
                 /*   val request = OneTimeWorkRequestBuilder<WifiWorkManager>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(data)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

                Sensors.NEARBY_DEVICES.sensor_name,
                Sensors.NUTRITION.sensor_name,
                Sensors.STEPS.sensor_name,
                Sensors.HEART_RATE.sensor_name,
                Sensors.BLOOD_GLUCOSE.sensor_name,
                Sensors.BLOOD_PRESSURE.sensor_name,
                Sensors.OXYGEN_SATURATION.sensor_name,
                Sensors.BODY_TEMPERATURE.sensor_name,
                -> {
                   /* val request =
                        OneTimeWorkRequestBuilder<GoogleHealthConnectWorkManager>().setExpedited(
                            OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
                        ).build()
                    WorkManager.getInstance(context).enqueue(request)*/
                }

            }
        }

        return@withContext Result.success()
    }

}