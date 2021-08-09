package digital.lamp.mindlamp.sheduleing

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils

class PeriodicScheduleWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val text =getInputData().getString("notification")
        val notificationId = inputData.getInt(ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,0)

        val oActivityDao = AppDatabase.getInstance(context).activityDao()
        val activityList = oActivityDao.getActivityList()
        activityList.forEach { activitySchedule ->
            activitySchedule.schedule?.forEach { durationIntervalLegacy ->
                LampLog.e("BROADCASTRECEIVER","invokeLocalNotification 2")
                durationIntervalLegacy.notification_ids?.forEach {
                    if(Utils.getMyIntValue(it) == notificationId){
                        LampLog.e("BROADCASTRECEIVER","invokeLocalNotification 3")
                        LampLog.e(
                                OneTimeScheduleWorker.TAG,
                            "Activity Name :: - ${activitySchedule.name} ---- $notificationId"
                        )
                        LampNotificationManager.showActivityNotification(
                            context,
                            activitySchedule,
                            notificationId
                        )
                    }
                }
            }
        }

        /*val notification = NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL)
            .setContentTitle("Scheduled notification $text")
            .setContentText("Hello from one-time worker")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_launcher_round
                )
            )
            .setVibrate(longArrayOf(0L))
            .setAutoCancel(true)
        Log.e("OneTimeScheduleWorker","OneTimeScheduleWorker 2")
        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("OneTimeScheduleWorker","OneTimeScheduleWorker 3")
            val activityChannel = NotificationChannel(
                AppConstants.NOTIFICATION_ACTIVITY,
                "channel1",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(activityChannel)
        }
        Log.e("OneTimeScheduleWorker","OneTimeScheduleWorker 4")
        manager.notify(notificationId, notification.build())
        Log.e("OneTimeScheduleWorker","OneTimeScheduleWorker 5")*/
        return Result.success()
    }
}