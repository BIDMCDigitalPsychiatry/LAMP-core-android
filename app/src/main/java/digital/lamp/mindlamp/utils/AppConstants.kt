package digital.lamp.mindlamp.utils

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 * This object class responsible for the constants used in the all other classes
 */

internal object AppConstants {
    const val BASE_URL : String = "https://api.lamp.digital/"
    const val NOTIFICATION_CHANNEL = "lamp_channel"
    const val NOTIFICATION_SURVEY_WITHOUT_ACTION = "lamp_survey_without_action"
    const val NOTIFICATION_SURVEY_WITH_ACTION = "lamp_survey_with_action"
    const val NOTIFICATION_SURVEY_OPEN = "lamp_survey_open"
    const val NOTIFICATION_ACTIVITY = "lamp_activity"
    const val NOTIFICATION_ID = 101
    const val INITIAL_TRIGGER = 120000L //30 seconds
    const val ALARM_INTERVAL = 300000L/* 600000L*/ //5 Minutes
    const val SYNC_TIME_STAMP_INTERVAL = 300000L//5 mins
    const val DAY_INTERVAL = 86400000L //24 Hours
    const val SYNC_SENSOR_SPEC_INTERVAL = 1 * 60 * 60 *1000L// 1hour

    const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    const val REQUEST_ID_TELEPHONY_PERMISSIONS = 2
    const val REQUEST_ID_WIFI_PERMISSIONS = 3
    const val PERMISSION_REQUEST_CODE = 123
    const val LOCATION_REQUEST_CODE = 100
    const val BLUETOOTH_REQUEST_CODE  = 200
    const val BLUETOOTH_REQUEST_RESULT_CODE  = 201
    const val HEALTH_CONNECT_PERMISSION_RESULT_CODE = 5000


    const val JAVASCRIPT_OBJ_LOGIN = "login"
    const val JAVASCRIPT_OBJ_LOGOUT = "logout"
    const val BASE_URL_WEB = "https://dashboard.lamp.digital/#/"
    const val MAIN_PAGE_URL = "https://dashboard.lamp.digital/#/?a="

    const val REPEAT_DAILY = 123456
    const val REPEAT_HOURLY = 122
    const val NOTIFICATION_DEFAULT = 101010

}