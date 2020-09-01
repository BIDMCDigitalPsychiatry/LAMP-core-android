package digital.lamp.mindlamp.notification

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import digital.lamp.mindlamp.activity.MainWearActivity
import digital.lamp.mindlamp.activity.SplashActivity
import digital.lamp.mindlamp.app.App


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