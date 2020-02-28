package digital.lamp.mindlamp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_CHANNEL

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
object LampNotificationManager {

    fun showNotification(context: Context, message: String) : Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                message,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle("MindLamp Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_stat_autorenew)
            .build()
    }

}