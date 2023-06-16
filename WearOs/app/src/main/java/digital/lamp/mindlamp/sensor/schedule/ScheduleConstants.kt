package digital.lamp.mindlamp.sensor.schedule

object ScheduleConstants {
    const val WORK_MANAGER_TAG = "local_notification_worker"

    const val SYNC_WORK_MANAGER_TAG = "sync_worker"
    const val SYNC_DATA_WORK_NAME = "sync_data_work"

    enum class WorkManagerParams(val value: String) {
        NOTIFICATION_ID("notification_id"),
        REPEAT_INTERVAL("repeat_interval")
    }
}