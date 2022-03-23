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
import digital.lamp.mindlamp.HomeActivity
import digital.lamp.mindlamp.NotificationActionActivity
import digital.lamp.mindlamp.NotificationActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.entity.ActivitySchedule
import digital.lamp.mindlamp.model.ActionData
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_ACTIVITY
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_CHANNEL
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_SURVEY_OPEN
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_SURVEY_WITHOUT_ACTION
import digital.lamp.mindlamp.utils.AppConstants.NOTIFICATION_SURVEY_WITH_ACTION
import java.util.concurrent.TimeUnit

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
object LampNotificationManager {

    fun showNotification(context: Context, message: String): Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                message,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.setSound(null, null)
            val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle("MindLamp Service")
                .setContentText(message)
                .setSound(null)
                .setVibrate(longArrayOf(0L))
                .setSmallIcon(R.drawable.ic_noti_icon)
                .build()
    }

    fun notificationWithActionButton(
        context: Context,
        remoteMessage: RemoteMessage,
        actionList: List<ActionData>
    ) {

        //Notification with open App Button with value
        val notificationIntent = Intent(context, NotificationActivity::class.java)
        val notificationId = remoteMessage.data["notificationId"]?.get(0)?.toInt()
        notificationIntent.putExtra("survey_path", remoteMessage.data["page"])
        notificationIntent.putExtra("notification_id", notificationId)
        notificationIntent.putExtra("remote_message", remoteMessage.data.toString())

        val pendingIntent = notificationId?.let {
            PendingIntent.getActivity(
                    context,
                    it, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        }

        val actionIntent = Intent(context, NotificationActionActivity::class.java)
        actionIntent.putExtra("survey_path", actionList[0].page)
        actionIntent.putExtra("notification_id", remoteMessage.data["notificationId"]!!.toInt())
        actionIntent.putExtra("remote_message", remoteMessage.data.toString())

        val actionPendingIntent = notificationId?.let {
            PendingIntent.getActivity(
                    context,
                    it, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        }

        val notification =
                NotificationCompat.Builder(context.applicationContext, NOTIFICATION_CHANNEL)
                        .setContentTitle(remoteMessage.data["title"])
                        .setContentText(remoteMessage.data["message"])
                        .setSmallIcon(R.drawable.ic_noti_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.ic_launcher_round
                            )
                        )
                        .setAutoCancel(true)
                        .setTimeoutAfter(remoteMessage.data["expiry"]!!.toLong())
                        .addAction(
                            R.drawable.ic_noti_icon,
                            actionList[0].name,
                            actionPendingIntent
                        )
                        .setContentIntent(actionPendingIntent)
                        .setOngoing(true)

        val manager =
                context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_SURVEY_WITH_ACTION,
                context.getString(R.string.channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(notificationId!!, notification.build())

    }

    fun notificationWithoutAction(context: Context, remoteMessage: RemoteMessage) {
        val notificationId = remoteMessage.data["notificationId"]?.get(0)?.toInt()
        val notificationIntent = Intent(context, NotificationActionActivity::class.java)
        notificationIntent.putExtra("survey_path", remoteMessage.data["page"])
        notificationIntent.putExtra("notification_id", notificationId)
        notificationIntent.putExtra("remote_message", remoteMessage.data.toString())

        val pendingIntent = notificationId?.let {
            PendingIntent.getActivity(
                    context,
                    it, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        }

        val notification =
                NotificationCompat.Builder(context.applicationContext, NOTIFICATION_CHANNEL)
                        .setContentTitle(remoteMessage.data["title"])
                        .setContentText(remoteMessage.data["message"])
                        .setSmallIcon(R.drawable.ic_noti_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.ic_launcher_round
                            )
                        )
                        .setAutoCancel(true)
                        .setTimeoutAfter(remoteMessage.data["expiry"]!!.toLong())
                        .setContentIntent(pendingIntent)

        val manager =
                context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_SURVEY_WITHOUT_ACTION,
                context.getString(R.string.channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(notificationId!!, notification.build())

    }

    fun notificationOpenApp(context: Context, remoteMessage: RemoteMessage) {
        val homeIntent = Intent(context, HomeActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val homePendingIntent = PendingIntent.getActivity(
            context,
            0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
                NotificationCompat.Builder(context.applicationContext, NOTIFICATION_CHANNEL)
                        .setContentTitle(remoteMessage.data["title"])
                        .setContentText(remoteMessage.data["message"])
                        .setSmallIcon(R.drawable.ic_noti_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.ic_launcher_round
                            )
                        )
                        .setAutoCancel(true)
                        .setTimeoutAfter(remoteMessage.data["expiry"]!!.toLong())
                        .setContentIntent(homePendingIntent)

        val manager =
                context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_SURVEY_OPEN,
                context.getString(R.string.channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val notificationId = remoteMessage.data["notificationId"]?.get(0)?.toInt()
        manager.notify(notificationId!!, notification.build())
    }

    fun showActivityNotification(
        context: Context,
        oActivitySchedule: ActivitySchedule,
        localNotificationId: Int
    ) {
        val actionIntent = Intent(context, NotificationActionActivity::class.java)
        actionIntent.putExtra(
            "survey_path",
            "participant/${AppState.session.userId}/activity/${oActivitySchedule.id}"
        )
        actionIntent.putExtra("notification_id", localNotificationId)
        val actionPendingIntent = PendingIntent.getActivity(
            context,
                localNotificationId, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val timeOut =  TimeUnit.HOURS.toMillis((1).toLong())
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(oActivitySchedule.name)
                .setContentText(
                   String.format(
                        context.getString(R.string.local_notification_text),
                        oActivitySchedule.name
                    )
                )
                .setSmallIcon(R.drawable.ic_noti_icon)
                .addAction(
                    R.drawable.ic_noti_icon,
                    context.getString(R.string.notification_action),
                    actionPendingIntent
                )
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.ic_launcher_round
                    )
                )
                .setVibrate(longArrayOf(0L))
                .setAutoCancel(true)
                .setContentIntent(actionPendingIntent)
            .setTimeoutAfter(timeOut)
                .setOngoing(true)



        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val activityChannel = NotificationChannel(
                NOTIFICATION_ACTIVITY,
                oActivitySchedule.id,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(activityChannel)
        }
        manager.notify(localNotificationId, notification.build())

    }
}