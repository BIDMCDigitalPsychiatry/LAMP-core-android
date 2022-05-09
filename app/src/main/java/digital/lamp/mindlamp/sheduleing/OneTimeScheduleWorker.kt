package digital.lamp.mindlamp.sheduleing

import android.content.Context
import androidx.work.*
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.sheduleing.ScheduleConstants.WORK_MANAGER_TAG
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class OneTimeScheduleWorker(
        private val context: Context,
        workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        val TAG = "OneTimeScheduleWorker"
    }

    override suspend fun doWork(): Result {

        val notificationId =
                inputData.getInt(ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value, 0)
        val repeatInterval =
                inputData.getString(ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value)

        val oActivityDao = AppDatabase.getInstance(context).activityDao()
        val activityList = oActivityDao.getActivityList()
        activityList.forEach { activitySchedule ->
            activitySchedule.schedule?.forEach { durationIntervalLegacy ->
                LampLog.e("BROADCASTRECEIVER", "invokeLocalNotification 2")
                durationIntervalLegacy.notification_ids?.forEach {
                    if (Utils.getMyIntValue(it) == notificationId) {
                        LampLog.e("BROADCASTRECEIVER", "invokeLocalNotification 3")
                        LampLog.e(
                                TAG,
                                "Activity Name :: - ${activitySchedule.name} ---- $notificationId"
                        )
                      //  if(!Utils.isOnline(context))
                        LampNotificationManager.showActivityNotification(
                                context,
                                activitySchedule,
                                notificationId
                        )
                    }
                }
            }
        }


        var interval = 0L
        var timeUnit = TimeUnit.MILLISECONDS
        var repeatNotification = false
        when (repeatInterval) {
            RepeatInterval.HOURLY.tag -> {
                interval = 1
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.EVERY_3H.tag -> {
                interval = 3
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.EVERY_6H.tag -> {
                interval = 6
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.EVERY_12H.tag -> {
                interval = 12
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.DAILY.tag -> {
                interval = 24
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.BIWEEKLY.tag -> {
                repeatNotification = false
                scheduleBiWeeklyNotification(notificationId)

            }
            RepeatInterval.WEEKLY.tag -> {
                interval = 24 * 7
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.FORTNIGHTLY.tag -> {
                interval = 24 * 14
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
            RepeatInterval.TRIWEEKLY.tag -> {
                repeatNotification = false
                scheduleTriWeeklyNotification(notificationId)
            }
            RepeatInterval.BIMONTHLY.tag -> {
                repeatNotification = false
                scheduleBiMonthlyNotification(notificationId)
            }
            RepeatInterval.MONTHLY.tag -> {
                repeatNotification = false
                setMonthlyNotification(notificationId)

            }
            RepeatInterval.CUSTOM.tag->{
                interval = 24
                timeUnit = TimeUnit.HOURS
                repeatNotification = true
            }
        }

        if (repeatNotification) {
            val data = Data.Builder()
            data.putInt(ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value, notificationId)
            val periodicWork =
                    PeriodicWorkRequestBuilder<PeriodicScheduleWorker>(
                            interval, timeUnit)
                            .addTag(WORK_MANAGER_TAG)
                            .setInputData(data.build())
                            .build()

            WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                            notificationId.toString(),
                            ExistingPeriodicWorkPolicy.REPLACE,
                            periodicWork
                    )
        }
        return Result.success()
    }

    private fun setMonthlyNotification(notificationId: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val nextReminderTime = calendar.timeInMillis
        val data = Data.Builder()
        data.putString(
                ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
                RepeatInterval.BIMONTHLY.tag
        )
        notificationId?.let {
            data.putInt(
                    ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                    it
            )
        }

        val work =
                OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                        .setInitialDelay(nextReminderTime, TimeUnit.MILLISECONDS)
                        .setInputData(data.build())
                        .addTag(WORK_MANAGER_TAG)
                        .build()

        WorkManager.getInstance(context).enqueue(work)
    }

    private fun scheduleTriWeeklyNotification(notificationId: Int) {
        val calendar = Calendar.getInstance()
        var mondayTimeMillis = 0L
        var wednesdayTimeMillis = 0L
        var fridayTimeMillis = 0L
        var currentDayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        mondayTimeMillis = calendar.timeInMillis

        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        wednesdayTimeMillis = calendar.timeInMillis

        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.FRIDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        fridayTimeMillis = calendar.timeInMillis


        val highestInterval = Math.max(mondayTimeMillis, Math.max(wednesdayTimeMillis, fridayTimeMillis))
        val secondSmallestInterval = Math.max(mondayTimeMillis, Math.min(wednesdayTimeMillis, fridayTimeMillis))
        val smallestInterval = Math.min(mondayTimeMillis, Math.min(wednesdayTimeMillis, fridayTimeMillis))

        val data = Data.Builder()
        data.putString(
                ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
                RepeatInterval.TRIWEEKLY.tag
        )
        notificationId?.let {
            data.putInt(
                    ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                    it
            )
        }

        var delay = highestInterval
        if (smallestInterval > System.currentTimeMillis())
            delay = smallestInterval
        else if (secondSmallestInterval > System.currentTimeMillis())
            delay = secondSmallestInterval


        val work =
                OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(data.build())
                        .addTag(WORK_MANAGER_TAG)
                        .build()

        WorkManager.getInstance(context).enqueue(work)
    }

    private fun scheduleBiWeeklyNotification(notificationId: Int) {
        val calendar = Calendar.getInstance()
        var tuesdayTimeMillis = 0L
        var thursdayTimeMillis = 0L
        var currentDayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.TUESDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        tuesdayTimeMillis = calendar.timeInMillis

        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        while (currentDayOfWeek != Calendar.THURSDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        thursdayTimeMillis = calendar.timeInMillis

        val data = Data.Builder()
        data.putString(
                ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
                RepeatInterval.BIWEEKLY.tag
        )
        notificationId?.let {
            data.putInt(
                    ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                    it
            )
        }

        var smallestInterval = Math.min(tuesdayTimeMillis, thursdayTimeMillis)
        var highestInterval = Math.max(tuesdayTimeMillis, thursdayTimeMillis)

        var delay = highestInterval
        if (smallestInterval > System.currentTimeMillis())
            delay = smallestInterval

        val work =
                OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(data.build())
                        .addTag(WORK_MANAGER_TAG)
                        .build()

        WorkManager.getInstance(context).enqueue(work)
    }

    private fun scheduleBiMonthlyNotification(notificationId: Int) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val mins = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONDAY)
        calendar.set(year, month, 10, hour, mins, seconds)
        val dateTenMillis = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 10)
        val dateTwentyMillis = calendar.timeInMillis

        val shortestInterval = min(dateTenMillis, dateTwentyMillis)
        val longestInterval = max(dateTenMillis, dateTwentyMillis)

        var delay = longestInterval
        if (shortestInterval > System.currentTimeMillis())
            delay = shortestInterval

        val data = Data.Builder()
        data.putString(
                ScheduleConstants.WorkManagerParams.REPEAT_INTERVAL.value,
                RepeatInterval.BIMONTHLY.tag
        )
        notificationId?.let {
            data.putInt(
                    ScheduleConstants.WorkManagerParams.NOTIFICATION_ID.value,
                    it
            )
        }

        val work =
                OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(data.build())
                        .addTag(WORK_MANAGER_TAG)
                        .build()

        WorkManager.getInstance(context).enqueue(work)
    }
}
