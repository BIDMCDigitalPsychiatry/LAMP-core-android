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

class PeriodicDataSyncWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        LampLog.e("Sensor PeriodicDataSyncWorker: start")
        if (AppState.session.lastAnalyticsTimestamp == AppState.session.lastSyncWorkerTimestamp) {
            LampLog.e("Sensor PeriodicDataSyncWorker: last times same start worker")
            syncAnalyticsData(context)
        }


        return Result.success()
    }

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
                    LampLog.e("Google Fit sync: ${gsonWithNull.toJson(googleFitData)}")
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
            LampLog.e("DB : ${list.size} and Sensor PeriodicDataSyncWorker: ${sensorEventDataList.size}")
            if (sensorEventDataList.isNotEmpty())
                invokeAddSensorData(sensorEventDataList,context,false)
            if (googleFitSensorEventDataList.isNotEmpty())
                invokeAddSensorData(googleFitSensorEventDataList,context,true)
            else {
                LampLog.e("Sensor PeriodicDataSyncWorker: sensorEventDataList is empty")

                val dbList = oAnalyticsDao.getAnalyticsList(AppState.session.lastAnalyticsTimestamp)
                if (dbList.isNotEmpty()) {
                    LampLog.e("Sensor PeriodicDataSyncWorker: dbList is not empty")
                    val analytics =
                            oAnalyticsDao.getFirstAnalyticsRecord(AppState.session.lastAnalyticsTimestamp)
                    AppState.session.lastAnalyticsTimestamp = analytics?.datetimeMillisecond ?: AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL

                    AppState.session.lastSyncWorkerTimestamp = analytics?.datetimeMillisecond ?: AppState.session.lastAnalyticsTimestamp + AppConstants.SYNC_TIME_STAMP_INTERVAL
                    syncAnalyticsData(context)
                }
            }
        }
    }

    private fun invokeAddSensorData(sensorEventDataList: ArrayList<SensorEvent>, context: Context, isGogolefitData :Boolean) {
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

    private fun trackSingleEvent(eventName: String) {
        //Firebase Event Tracking
        val params = Bundle()
        Firebase.analytics.logEvent(eventName, params)
    }
}