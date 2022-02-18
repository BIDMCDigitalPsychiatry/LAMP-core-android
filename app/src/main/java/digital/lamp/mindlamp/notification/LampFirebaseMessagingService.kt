package digital.lamp.mindlamp.notification

import android.net.TrafficStats
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import digital.lamp.lamp_kotlin.lamp_core.apis.SensorEventAPI
import digital.lamp.lamp_kotlin.lamp_core.models.NotificationData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.HomeActivity
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.model.*
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.Utils.isServiceRunning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by ZCO Engineering Dept. on 23,April,2020
 */
class LampFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e(TAG, "Remote Message: ${remoteMessage.data}")

        DebugLogs.writeToFile(remoteMessage.data.toString())
        val gson = Gson()
        var actionList: List<ActionData> = listOf()
        if (remoteMessage.data["actions"] != null)
         actionList = gson.fromJson(remoteMessage.data["actions"], object : TypeToken<List<ActionData?>?>() {}.type) as List<ActionData>

        //Notification with page and action Button
        if (remoteMessage.data["page"] != null && remoteMessage.data["page"]!!.isNotEmpty() && actionList.isNotEmpty()) {
            LampNotificationManager.notificationWithActionButton(this, remoteMessage, actionList)
        }
        //Notification with page and no action Button
        else if (remoteMessage.data["page"] != null && remoteMessage.data["page"]!!.isNotEmpty()) {
            LampNotificationManager.notificationWithoutAction(this, remoteMessage)
        } else if (remoteMessage.data["command"] != null) {
            invokeDiagnosticData()
        }
        //Notification with page empty or action empty
        else {
            LampNotificationManager.notificationOpenApp(this, remoteMessage)
        }


        //Call Analytics API
        if (AppState.session.isLoggedIn) {
            val notificationData =
                    NotificationData("notification", "Open App", remoteMessage.data.toString())

            val notificationEvent =
                    SensorEvent(
                            notificationData,
                            "lamp.analytics", System.currentTimeMillis().toDouble()
                    )
            invokeNotificationData(notificationEvent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token: $token")
    }

    companion object {
        private val TAG = LampFirebaseMessagingService::class.java.simpleName
    }

    private fun invokeNotificationData(notificationEventRequest: SensorEvent) {
        val basic = "Basic ${
            Utils.toBase64(
                    AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                            "https://"
                    ).removePrefix("http://")
            )
        }"
        Thread {
            TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt()) // <---

            // Do network action in this function
            val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                    AppState.session.userId,
                    notificationEventRequest,
                    basic
            )
            LampLog.e(TAG, " Notification Data Send -  $state")
        }.start()

    }

    private fun invokeDiagnosticData() {
        val storage = DiagnosticStorage(Utils.getTotalInternalMemorySize(), Utils.getAvailableInternalMemorySize())
        val sensorDao = AppDatabase.getInstance(this).sensorDao()
        val analyticsDao = AppDatabase.getInstance(this).analyticsDao()
        var sensorSpecList = listOf<SensorSpecs>()
        var configuredSensors = listOf<String>()
        var pendingRecordCount = 0
        GlobalScope.launch(Dispatchers.IO) {
            sensorSpecList = sensorDao.getSensorsList()
            pendingRecordCount = analyticsDao.getNumberOfRecordsToSync(AppState.session.lastAnalyticsTimestamp)


            val pendingData = PendingData(AppState.session.lastAnalyticsTimestamp.toString(), pendingRecordCount.toString())




            if (sensorSpecList.isEmpty()) {
                val sensorsList: Array<Sensors> = Sensors.values()
                sensorsList.toCollection(ArrayList())
                configuredSensors = sensorsList.map { it.sensor_name }
            } else {
                configuredSensors = sensorSpecList.map { it.spec!! }
            }

            val diagnosticDataContent = DiagnosticDataContent(storage, configuredSensors, false, AppState.session.isLoggedIn,
                    Utils.isDeviceIsInPowerSaveMode(this@LampFirebaseMessagingService), this@LampFirebaseMessagingService.isServiceRunning(LampForegroundService::class.java),
                    NetworkUtils.isWifiNetworkAvailable(this@LampFirebaseMessagingService), Utils.getLocationAuthorizationStatus(this@LampFirebaseMessagingService), pendingData)
            val diagnosticData =
                    DiagnosticData("diagnostic", diagnosticDataContent, "Android",  Utils.getUserAgent())

            val diagnosticEvent =
                    SensorEvent(
                            diagnosticData,
                            "lamp.analytics", System.currentTimeMillis().toDouble()
                    )

            val basic = "Basic ${Utils.toBase64(
                    AppState.session.token + ":" + AppState.session.serverAddress.removePrefix(
                            "https://"
                    ).removePrefix("http://")
            )}"

            val state = SensorEventAPI(AppState.session.serverAddress).sensorEventCreate(
                    AppState.session.userId,
                    diagnosticEvent,
                    basic
            )
            LampLog.e(TAG, "diagnostic data send -  $state")
            DebugLogs.writeToFile("Diagnostic Data Send")

        }
    }
}