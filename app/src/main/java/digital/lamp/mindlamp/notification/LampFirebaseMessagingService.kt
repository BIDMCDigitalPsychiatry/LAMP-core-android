package digital.lamp.mindlamp.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by ZCO Engineering Dept. on 23,April,2020
 */
class LampFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e(TAG, "Message: ${remoteMessage.notification?.title}")
        Log.e(TAG, "Remote Message: ${remoteMessage.data["title"]}")
        Log.e(TAG, "Remote Message: ${remoteMessage.data["message"]}")

        LampNotificationManager.showMessageNotification(this,remoteMessage)


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token: $token")
    }

    companion object{
        private val TAG = LampFirebaseMessagingService::class.java.simpleName
    }

}