package digital.lamp.mindlamp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_CHANNEL

/**
 * Created by ZCO Engineering Dept
 */
object LampNotificationManager {

    fun displayNotification(context: Context, message: String, title: String) {

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_stat_noti_icon,
            title,
            null
        ).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* // Create an intent for the reply action
             val actionPendingIntent = Intent(this, ActionActivity::class.java).let { actionIntent ->
                 PendingIntent.getActivity(this, 0, actionIntent,
                     PendingIntent.FLAG_UPDATE_CURRENT)
             }*/
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                message,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.setSound(null, null)
            val manager =
                context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(null)
            .setVibrate(longArrayOf(0L))
            .extend(NotificationCompat.WearableExtender().addAction(action))
            .setSmallIcon(R.drawable.ic_stat_noti_icon)
            .build()

        val manager =
            context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(5, notification)
    }

}