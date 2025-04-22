package digital.lamp.mindlamp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorAPI
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.SensorSpec
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Locations
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Sensors
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationWorkManager(private val context: Context, parameters: WorkerParameters) :
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
            //Location Settings
            Lamp.startLocations(context)
            frequency.let {
                val interval = (1 / frequency) * 1000
                Locations.setInterval(interval.toLong())
            }
            //Location Observer
            Locations.setSensorObserver { data ->
                LampLog.e(data.toString())
                if (data != null) {
                    val dimensionData =
                        DimensionData(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            data.longitude,
                            data.latitude,
                            data.altitude,
                            data.accuracy,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null, null, null, null, null, null, null
                        )
                    val sensorEventData =
                        SensorEvent(
                            dimensionData,
                            Sensors.GPS.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    getLocationData(sensorEventData)
                } else {
                    val logEventRequest = LogEventRequest()
                    logEventRequest.message = context.getString(R.string.log_location_null)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            val logEventRequest = LogEventRequest()
            logEventRequest.message = context.getString(R.string.log_location_error)
            invokeLogData(context)

        }
        return Result.success()
    }
    private fun invokeLogData(context: Context) {
        if (NetworkUtils.isNetworkAvailable(context) && NetworkUtils.getBatteryPercentage(context) > 15) {

            val sensorSpecsList: ArrayList<SensorSpecs> = arrayListOf()
            val basic = "Basic ${
                Utils.toBase64(
                    AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                        "https://"
                    ).removePrefix("http://")
                )
            }"

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val state = SensorAPI(AppState.session.serverAddress).sensorAll(
                        AppState.session.userId,
                        basic
                    )
                    val oSensorSpec: SensorSpec =
                        Gson().fromJson(state.toString(), SensorSpec::class.java)
                } catch (e: Exception) {

                }
            }
        }
    }
    private fun getLocationData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }
}