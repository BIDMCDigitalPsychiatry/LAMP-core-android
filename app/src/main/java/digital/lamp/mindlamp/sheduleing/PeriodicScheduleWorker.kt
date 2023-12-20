package digital.lamp.mindlamp.sheduleing

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils
/**
 * Worker class responsible for handling periodic schedule events.
 *
 * @param context The application context.
 * @param workerParams Parameters for the worker, including input data.
 */
class PeriodicScheduleWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    /**
     * Perform the work in a coroutine.
     *
     * @return The result of the work.
     */
    override suspend fun doWork(): Result {

        val text =getInputData().getString("notification")
        val notificationId = inputData.getInt(ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,0)

        val oActivityDao = AppDatabase.getInstance(context).activityDao()
        val activityList = oActivityDao.getActivityList()
        activityList.forEach { activitySchedule ->
            activitySchedule.schedule?.forEach { durationIntervalLegacy ->
                durationIntervalLegacy.notification_ids?.forEach {
                    if(Utils.getMyIntValue(it) == notificationId){
                        // Show a notification for the activity
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