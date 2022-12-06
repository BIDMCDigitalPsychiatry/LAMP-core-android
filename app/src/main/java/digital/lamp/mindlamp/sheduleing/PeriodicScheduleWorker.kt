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
        return Result.success()
    }
}