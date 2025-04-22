package digital.lamp.mindlamp.workers

import android.content.Context
import android.net.http.NetworkException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorAPI
import digital.lamp.lamp_kotlin.lamp_core.models.SensorSpec
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.SensorDao
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LampWorkManager(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private lateinit var oSensorDao: SensorDao

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val context = applicationContext
        DebugLogs.writeToFile("Call sensor spec (WorkManager)")
        oSensorDao = AppDatabase.getInstance(context).sensorDao()
        // Check network and battery constraints
        if (!NetworkUtils.isNetworkAvailable(context)) {
            DebugLogs.writeToFile("Network unavailable")
            return@withContext Result.retry()
        }

        if (NetworkUtils.getBatteryPercentage(context) <= 15) {
            DebugLogs.writeToFile("Battery low")
            return@withContext Result.retry()
        }

        try {
            // Validate session
            val session = AppState.session ?: run {
                DebugLogs.writeToFile("Session is null")
                return@withContext Result.failure()
            }

            // Construct basic authorization header
            val basic = "Basic ${
                Utils.toBase64(
                    session.token + ":" +
                            session.serverAddress.removePrefix("https://").removePrefix("http://")
                )
            }"

            // Fetch sensor specifications from the server
            val state = SensorAPI(session.serverAddress).sensorAll(session.userId, basic)
            val sensorSpecsList: ArrayList<SensorSpecs> = arrayListOf()
            val oSensorSpec: SensorSpec? = Gson().fromJson(state.toString(), SensorSpec::class.java)

            if (oSensorSpec?.data?.isNotEmpty() == true) {
                // Update session state
                session.isCellularUploadAllowed = oSensorSpec.data.any {
                    it.settings == null || it.settings?.cellular_upload != false
                }

                // Prepare sensor specs list
                oSensorSpec.data.forEach { sensor ->
                    val sensorSpecs = SensorSpecs(
                        null,
                        sensor.id,
                        sensor.spec,
                        sensor.name,
                        sensor.settings?.frequency,
                        sensor.settings?.cellular_upload
                    )
                    sensorSpecsList.add(sensorSpecs)
                }

                // Update database only if data changes
                val existingSensors = oSensorDao.getSensorsList()
                if (existingSensors != sensorSpecsList) {
                    oSensorDao.deleteSensorList()
                    oSensorDao.insertAllSensors(sensorSpecsList)
                }
            }

            DebugLogs.writeToFile("Sensor Spec Size - ${oSensorDao.getSensorsList().size}")

            // Handle initial call logic
            if (inputData.getBoolean("initialCall", false)) {
                val request = OneTimeWorkRequestBuilder<SensorCoordinatorWorker>().build()
                WorkManager.getInstance(context).enqueue(request)
            }

            return@withContext Result.success()

        } catch (e: Exception) {
            DebugLogs.writeToFile("SensorSpecWorker failed: ${e.message}")
            return@withContext if (e is NetworkException) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}