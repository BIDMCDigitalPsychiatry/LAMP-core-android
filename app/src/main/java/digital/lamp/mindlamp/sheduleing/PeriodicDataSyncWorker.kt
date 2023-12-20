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
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

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

        val googleFitSensorEventDataList: ArrayList<SensorEvent> = arrayListOf<SensorEvent>()
        googleFitSensorEventDataList.clear()

        val gson = GsonBuilder()
            .create()

        val gsonWithNull = GsonBuilder().serializeNulls()
            .create()
        GlobalScope.launch(Dispatchers.IO) {
            val list: List<Analytics>
            val oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()

            if (AppState.session.lastAnalyticsTimestamp == 1L) {
                val analytics =
                    oAnalyticsDao.getFirstAnalyticsRecord(AppState.session.lastAnalyticsTimestamp)
                AppState.session.lastAnalyticsTimestamp = analytics?.datetimeMillisecond ?: 1L
            }
            val endTime =
                AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL
            list = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp, endTime)


            list.forEach {
                val sensorEvent = gson.fromJson(
                    it.analyticsData,
                    SensorEvent::class.java
                )
                if (sensorEvent.sensor == Sensors.SLEEP.sensor_name || sensorEvent.sensor == Sensors.NUTRITION.sensor_name ||
                    sensorEvent.sensor == Sensors.STEPS.sensor_name || sensorEvent.sensor == Sensors.HEART_RATE.sensor_name ||
                    sensorEvent.sensor == Sensors.BLOOD_GLUCOSE.sensor_name || sensorEvent.sensor == Sensors.BLOOD_PRESSURE.sensor_name
                    || sensorEvent.sensor == Sensors.OXYGEN_SATURATION.sensor_name || sensorEvent.sensor == Sensors.BODY_TEMPERATURE.sensor_name
                ) {
                    val googleFitData = gsonWithNull.fromJson(
                        it.analyticsData,
                        SensorEvent::class.java
                    )

                    googleFitSensorEventDataList.add(
                        googleFitData
                    )
                } else {
                    sensorEventDataList.add(
                        sensorEvent
                    )
                }
            }
            list.let {
                if (it.isNotEmpty()) {
                    AppState.session.lastAnalyticsTimestamp = it[0].datetimeMillisecond!!
                    AppState.session.lastSyncWorkerTimestamp = it[0].datetimeMillisecond!!
                }
            }
            if (sensorEventDataList.isNotEmpty())
                invokeAddSensorData(sensorEventDataList, context, false)
            if (googleFitSensorEventDataList.isNotEmpty())
                invokeAddSensorData(googleFitSensorEventDataList, context, true)
            else {
                val dbList = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                if (dbList.isNotEmpty()) {
                    val analytics =
                        oAnalyticsDao.getFirstAnalyticsRecord(AppState.session.lastAnalyticsTimestamp)
                    AppState.session.lastAnalyticsTimestamp = analytics?.datetimeMillisecond
                        ?: (AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL)

                    AppState.session.lastSyncWorkerTimestamp = analytics?.datetimeMillisecond
                        ?: (AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL)
                    syncAnalyticsData(context)
                }
            }
        }
    }

    /**
     * Send data to api
     */
    private fun invokeAddSensorData(
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
            DebugLogs.writeToFile("API Send : ${sensorEventDataList.size}")
            trackSingleEvent("API_Send_${sensorEventDataList.size}")

            val basic = "Basic ${
                Utils.toBase64(
                    AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                        "https://"
                    ).removePrefix("http://")
                )
            }"

            TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---
            try {
                val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                    AppState.session.userId,
                    sensorEventDataList,
                    basic, isGogolefitData
                )
                if (state.isNotEmpty()) {
                    //Code for drop DB
                    GlobalScope.launch(Dispatchers.IO) {
                        val oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
                        oAnalyticsDao.deleteAnalyticsList(AppState.session.lastAnalyticsTimestamp)
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