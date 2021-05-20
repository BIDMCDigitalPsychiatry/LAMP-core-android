package digital.lamp.mindlamp.sheduleing

object ScheduleConstants {

    const val EVERY_3H = 120000L //30 seconds
    const val EVERY_6H = 21600000L
    const val EVERY_12H = 43200000L

    const val WORK_MANAGER_TAG = "local_notification_worker"

    enum class WorkManagerParams(val value:String){
        NOTIFICATION_ID("notification_id"),
        REPEAT_INTERVAL("repeat_interval")
    }
}