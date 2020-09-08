package digital.lamp.mindlamp.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created by ZCO Engineering Dept.
 */
class LampWearFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("Received Token", "Received token: ${remoteMessage.data.toString()}")

        val title = remoteMessage.data.get("title")
        val actionval = remoteMessage.data.get("action")
        val content = remoteMessage.data.get("content")


        //silent notification. get sensor vals
        if (content.isNullOrEmpty()) {

            val intentNotification = Intent()
            intentNotification.action = "com.from.notification"
            intentNotification.putExtra("title", title)
            intentNotification.putExtra("action", actionval)
            intentNotification.putExtra("content", content)
            applicationContext.sendBroadcast(intentNotification)


        } else {

            LampNotificationManager.displayNotification(
                this,
                content.toString(),
                title.toString()
            )

        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token: $token")
    }

    companion object {
        private val TAG = LampWearFirebaseMessagingService::class.java.simpleName
    }

}