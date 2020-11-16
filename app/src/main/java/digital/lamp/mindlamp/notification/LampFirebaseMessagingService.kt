package digital.lamp.mindlamp.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.ActionData
import digital.lamp.mindlamp.network.model.NotificationData
import digital.lamp.mindlamp.network.model.NotificationEventRequest
import digital.lamp.mindlamp.repository.HomeRepository
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 23,April,2020
 */
class LampFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e(TAG, "Remote Message: ${remoteMessage.data}")

        DebugLogs.writeToFile(remoteMessage.data.toString())
        val gson = Gson()
        val actionList: List<ActionData> = gson.fromJson(remoteMessage.data["actions"], object : TypeToken<List<ActionData?>?>() {}.type) as List<ActionData>

        //Notification with page and action Button
        if (remoteMessage.data["page"] != null && remoteMessage.data["page"]!!.isNotEmpty() && actionList.isNotEmpty()){
            LampNotificationManager.notificationWithActionButton(this,remoteMessage,actionList)
        }
        //Notification with page and no action Button
        else if(remoteMessage.data["page"] != null && remoteMessage.data["page"]!!.isNotEmpty()){
            LampNotificationManager.notificationWithoutAction(this,remoteMessage)
        }
        //Notification with page empty or action empty
        else{
            LampNotificationManager.notificationOpenApp(this,remoteMessage)
        }


        //Call Analytics API
        if (AppState.session.isLoggedIn) {
            val notificationData =
                NotificationData("notification", "Open App", remoteMessage.data.toString())
            val notificationEvent = NotificationEventRequest(
                notificationData,
                "lamp.analytics",
                System.currentTimeMillis()
            )
            invokeNotificationData(notificationEvent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token: $token")
    }

    companion object{
        private val TAG = LampFirebaseMessagingService::class.java.simpleName
    }

    private fun invokeNotificationData(notificationEventRequest: NotificationEventRequest) {
        val homeRepository = HomeRepository()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = homeRepository.addNotificationData(
                    AppState.session.userId,
                    notificationEventRequest
                )
                LampLog.e(TAG, " : $response")

            } catch (er: Exception) {
                er.printStackTrace()
            }
        }
    }
}