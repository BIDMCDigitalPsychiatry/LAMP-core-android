package digital.lamp.mindlamp.sheduleing

import android.content.Context
import android.net.TrafficStats
import android.os.Bundle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.utils.AppConstants
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Sensors
import digital.lamp.mindlamp.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * PeriodicDataSyncWorker used to send analytics data to server on a periodic basis
 *
 * @param appContext The application context.
 * @param workerParams Parameters to setup the worker, including input data.
 */
class PeriodicDataSyncWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    /**
     * This method is called in the background thread to perform the work.
     * It should return the result of the computation.
     *
     * @return The Result indicating success or failure of the work.
     */
    override suspend fun doWork(): Result {
        if (AppState.session.lastAnalyticsTimestamp == AppState.session.lastSyncWorkerTimestamp) {
            syncAnalyticsData(context)
        }


        return Result.success()
    }

    /**
     * Fetch data analytics data from db and send to server
     */

    private fun syncAnalyticsData(context: Context) {
        val sensorEventDataList: ArrayList<SensorEvent> = arrayListOf<SensorEvent>()
        sensorEventDataList.clear()

        val googleHealthConnectSensorEventDataList: ArrayList<SensorEvent> = arrayListOf<SensorEvent>()
        googleHealthConnectSensorEventDataList.clear()

        val gson = GsonBuilder()
            .create()

        val gsonWithNull = GsonBuilder().serializeNulls()
            .create()
        GlobalScope.launch(Dispatchers.IO) {
            val list: List<Analytics>
            LampLog.e("Sensor PeriodicDataSyncWorker: START TIME ${AppState.session.lastAnalyticsTimestamp}")
            val oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()

            if (AppState.session.lastAnalyticsTimestamp == 1L) {
                val analytics =
                    oAnalyticsDao.getFirstAnalyticsRecord(AppState.session.lastAnalyticsTimestamp)
                AppState.session.lastAnalyticsTimestamp = analytics?.datetimeMillisecond ?: 1L
            }
            LampLog.e("Sensor PeriodicDataSyncWorker: START TIME ${AppState.session.lastAnalyticsTimestamp}")
            val endTime =
                AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL
            LampLog.e("Sensor PeriodicDataSyncWorker: END TIME $endTime")
            list = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp, endTime)


            list.forEach {
                val sensorEvent = gson.fromJson(
                    it.analyticsData,
                    SensorEvent::class.java
                )
                sensorEvent?.let {sensorEvent->
                    if (sensorEvent.sensor == Sensors.SLEEP.sensor_name || sensorEvent.sensor == Sensors.NUTRITION.sensor_name ||
                        sensorEvent.sensor == Sensors.STEPS.sensor_name || sensorEvent.sensor == Sensors.HEART_RATE.sensor_name ||
                        sensorEvent.sensor == Sensors.BLOOD_GLUCOSE.sensor_name || sensorEvent.sensor == Sensors.BLOOD_PRESSURE.sensor_name
                        || sensorEvent.sensor == Sensors.OXYGEN_SATURATION.sensor_name || sensorEvent.sensor == Sensors.BODY_TEMPERATURE.sensor_name
                    ) {
                        val googleFitData = gsonWithNull.fromJson(
                            it.analyticsData,
                            SensorEvent::class.java
                        )

                        googleHealthConnectSensorEventDataList.add(
                            googleFitData
                        )
                    } else {
                        sensorEventDataList.add(
                            sensorEvent
                        )
                    }
                }

            }
            list.let {
                if (it.isNotEmpty()) {
                    AppState.session.lastAnalyticsTimestamp = it[0].datetimeMillisecond!!
                    AppState.session.lastSyncWorkerTimestamp = it[0].datetimeMillisecond!!
                }
            }
            LampLog.e("DB : ${list.size} and Sensor PeriodicDataSyncWorker: ${sensorEventDataList.size}")
            if (sensorEventDataList.isNotEmpty())
                invokeAddSensorData(sensorEventDataList, context, false)
            else {
                try {
                    LampLog.e("Sensor PeriodicDataSyncWorker: sensorEventDataList is empty")

                    //val dbList = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                    val anayticsRowCount = oAnalyticsDao.getNumberOfRecordsToSync(AppState.session.lastAnalyticsTimestamp)
                    if (anayticsRowCount>0) {
                        LampLog.e("Sensor PeriodicDataSyncWorker: dbList is not empty")
                        val analytics =
                            oAnalyticsDao.getFirstAnalyticsRecord(AppState.session.lastAnalyticsTimestamp)
                        AppState.session.lastAnalyticsTimestamp = analytics?.datetimeMillisecond
                            ?: (AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL)

                        AppState.session.lastSyncWorkerTimestamp = analytics?.datetimeMillisecond
                            ?: (AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL)
                        syncAnalyticsData(context)
                    }
                }catch (e:Exception){
                    DebugLogs.writeToFile("exception in PeriodicDataSyncWorker ${e.message}")
                }
            }
            if (googleHealthConnectSensorEventDataList.isNotEmpty())
                invokeAddSensorData(googleHealthConnectSensorEventDataList, context, true)

        }
    }

    /**
     * Send data to api
     */
    private suspend fun invokeAddSensorData(
        sensorEventDataList: ArrayList<SensorEvent>,
        context: Context,
        isGogolefitData: Boolean
    ) {
        if (!AppState.session.isCellularUploadAllowed && !NetworkUtils.isWifiNetworkAvailable(
                context
            )
        )
            return
        if (NetworkUtils.isNetworkAvailable(context) && NetworkUtils.getBatteryPercentage(context) > 15) {
            trackSingleEvent("API_Send ${sensorEventDataList.size}")

            TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---
            try {
                val state = Utils.apiWithRetry {
                    val basic = if (AppState.session.accessToken.isNotEmpty()){
                        "Bearer ${AppState.session.accessToken}"
                    }else {
                        "Basic ${
                            Utils.toBase64(
                                AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                                    "https://"
                                ).removePrefix("http://")
                            )
                        }"
                    }
                    SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                        AppState.session.userId,
                        sensorEventDataList,
                        basic, isGogolefitData
                    )
                }
                LampLog.e("PeriodicDataSyncWorker", " Lamp Core Response -  $state")
                if (state.isNotEmpty()) {
                    //Code for drop DB
                    GlobalScope.launch(Dispatchers.IO) {
                        val oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
                        oAnalyticsDao.deleteAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                        LampLog.e("Sensor : invokeAddSensorData")
                        syncAnalyticsData(context)
                    }
                }
            } catch (e: Exception) {
                DebugLogs.writeToFile("Exception PeriodicDataSyncWorker invokeAddSensorData:${e.printStackTrace()} \n ${e.message}")
            }
        }
    }
    /**
     * Track a single event using Firebase Analytics.
     *
     * @param eventName The name of the event to be tracked.
     */
    private fun trackSingleEvent(eventName: String) {
        //Firebase Event Tracking
        val params = Bundle()
        // Log the event using Firebase Analytics
        Firebase.analytics.logEvent(eventName, params)
    }
}