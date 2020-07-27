package digital.lamp.mindlamp.standalone.notification

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created by ZCO Engineering Dept.
 */
class LampWearFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("Received Token", "Received token: ${remoteMessage.data.toString()}")

        val intentNotification = Intent()
        intentNotification.action = "com.from.notification"
        sendBroadcast(intentNotification)

    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token: $token")
    }

    companion object {
        private val TAG = LampWearFirebaseMessagingService::class.java.simpleName
    }

}