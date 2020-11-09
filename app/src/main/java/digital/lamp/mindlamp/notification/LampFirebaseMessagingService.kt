package digital.lamp.mindlamp.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import digital.lamp.mindlamp.model.ActionData
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog


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

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token: $token")
    }

    companion object{
        private val TAG = LampFirebaseMessagingService::class.java.simpleName
    }

}