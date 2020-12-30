package digital.lamp.mindlamp.utils

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */

internal object AppConstants {
    const val BASE_URL : String = "https://api.lamp.digital/"
    const val NOTIFICATION_CHANNEL = "lamp_channel"
    const val NOTIFICATION_SURVEY_WITHOUT_ACTION = "lamp_survey_without_action"
    const val NOTIFICATION_SURVEY_WITH_ACTION = "lamp_survey_with_action"
    const val NOTIFICATION_SURVEY_OPEN = "lamp_survey_open"
    const val NOTIFICATION_ID = 101
    const val INITIAL_TRIGGER = 120000L //30 seconds
    const val ALARM_INTERVAL = 300000L //10 Minutes

    const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    const val JAVASCRIPT_OBJ_LOGIN = "login"
    const val JAVASCRIPT_OBJ_LOGOUT = "logout"
    const val BASE_URL_WEB = "https://dashboard.lamp.digital/#/"
    const val MAIN_PAGE_URL = "https://dashboard.lamp.digital/#/?a="

}