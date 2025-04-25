package digital.lamp.mindlamp.sheduleing

/**
 * This class represents constants used in workmanager
 */
object ScheduleConstants {
    const val WORK_MANAGER_TAG = "local_notification_worker"

    const val SYNC_WORK_MANAGER_TAG = "sync_worker"
    const val SYNC_DATA_WORK_NAME = "sync_data_work"

    const val LAMP_WORK_MANAGER_TAG = "lamp_work_manager"

    enum class WorkManagerParams(val value:String){
        NOTIFICATION_ID("notification_id"),
        REPEAT_INTERVAL("repeat_interval")
    }
}