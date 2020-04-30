package digital.lamp.mindlamp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.RemoteMessage
import digital.lamp.mindlamp.CustomWebviewActivity
import digital.lamp.mindlamp.HomeActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.SplashActivity
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_CHANNEL
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_ID

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
            .setSmallIcon(R.drawable.ic_stat_noti_icon)
            .build()
    }

    fun showMessageNotification(context: Context,remoteMessage: RemoteMessage){

        val notificationIntent: Intent

        val homeIntent = Intent(context, HomeActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val homePendingIntent = PendingIntent.getActivity(
            context,
            0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if(remoteMessage.data["page"] != null && remoteMessage.data.isNotEmpty()){
            notificationIntent = Intent(context, CustomWebviewActivity::class.java)
            notificationIntent.putExtra("survey_path",remoteMessage.data["page"])

            val pendingIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification =
                NotificationCompat.Builder(context.applicationContext, NOTIFICATION_CHANNEL)
                    .setContentTitle(remoteMessage.data["title"])
                    .setContentText(remoteMessage.data["message"])
                    .setSmallIcon(R.drawable.ic_stat_noti_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(context.resources,
                            R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_stat_noti_icon,"Open App",homePendingIntent)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
            val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(NOTIFICATION_CHANNEL,
                    context.getString(R.string.channel_description),
                    NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
            manager.notify(NOTIFICATION_ID, notification.build())

        }else{
            notificationIntent = Intent(context, HomeActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val pendingIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification =
                NotificationCompat.Builder(context.applicationContext, NOTIFICATION_CHANNEL)
                    .setContentTitle(remoteMessage.data["title"])
                    .setContentText(remoteMessage.data["message"])
                    .setSmallIcon(R.drawable.ic_stat_noti_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(context.resources,
                            R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(NOTIFICATION_CHANNEL,
                    context.getString(R.string.channel_description),
                    NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
            manager.notify(NOTIFICATION_ID, notification.build())
        }

    }

}