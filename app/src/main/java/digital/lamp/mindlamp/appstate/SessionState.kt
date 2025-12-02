package digital.lamp.mindlamp.appstate

/**
 * This class is represent preference variables used in project.
 */
class SessionState {

    companion object {
        const val PREF_KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val PREF_KEY_TOKEN = "token"

        const val PREF_KEY_REFRESH_TOKEN = "refreshToken"

        const val PREF_KEY_ACCESS_TOKEN = "accessToken"
        const val PREF_KEY_USER_ID = "user_id"
        const val PREF_KEY_SERVER_ADDRESS = "serverAddress"
        const val PREF_KEY_CRASH_VALUE = "crash_value"
        const val PREF_ANALYTICS_TIME_STAMP = "lastAnalyticsTimestamp"
        const val PREF_WORKER_TIME_STAMP = "lastSyncWorkerTimestamp"
        const val PREF_SHOW_DISCLOSURE_ALERT = "show_disclosure_alert"
        const val PREF_KEY_ALLOWED_CELLULAR_UPLOAD = "allowed_cellular_upload"
        const val PREF_LAST_SLEEP_DATA_TIME_STAMP = "last_sleep_data_timestamp"
        const val PREF_LAST_STEP_DATA_TIME_STAMP = "last_step_data_timestamp"
        const val PREF_GOOGLE_FIT_CONNECTED = "google_fit_connected"
        const val PREF_LOCATION_PERMISSION_ALLOWED = "location_permission_allowed"
        const val PREF_TELEPHONY_PERMISSION_ALLOWED = "location_permission_allowed"
        const val PREF_GOOGLE_FIT_DATA_TIME_STAMP = "last_google_fit_data_timestamp"

        const val PREF_LAST_SENSOR_DATA_TIMESTAMP = "lastSensorDataTimestamp"
        const val PREF_GOOGLE_HEALTH_CONNECT_CONNECTED = "googleHealthConnectConnected"

        const val PREF_LAST_SPEED_DATA_TIMESTAMP = "last_speed_data_timestamp"
        const val PREF_LAST_TOTAL_CALORIES__DATA_TIMESTAMP = "last_total_calories_data_timestamp"
        const val PREF_LAST_DISTANCE_DATA_TIMESTAMP = "last_distance_data_timestamp"
        const val PREF_LAST_HYDRATION_DATA_TIMESTAMP = "last_hydration_data_timestamp"
        const val PREF_LAST_HEART_RATE_DATA_TIMESTAMP = "last_heart_rate_data_timestamp"
        const val PREF_LAST_NUTRITION_DATA_TIMESTAMP = "last_nutrition_data_timestamp"
        const val PREF_LAST_BLOOD_GLUCOSE_DATA_TIMESTAMP = "last_blood_glucose_data_timestamp"
        const val PREF_LAST_BLOOD_PRESSURE_DATA_TIMESTAMP = "last_blood_pressure_data_timestamp"
        const val PREF_LAST_BODY_TEMPERATURE_DATA_TIMESTAMP = "last_body_temperature_data_timestamp"
        const val PREF_LAST_STEPS_CADENCE_DATA_TIMESTAMP = "last_steps_cadence_data_timestamp"
        const val PREF_LAST_RESPIRATORY_DATA_TIMESTAMP = "last_respiratory_data_timestamp"
        const val PREF_CURRENT_STREAK_DAYS = "current_streak_days"
        const val PREF_LONGEST_STREAK_DAYS = "longest_streak_days"
    }

    var isLoggedIn by Pref(
        PREF_KEY_IS_USER_LOGGED_IN,
        false
    )
    var token by Pref(
        PREF_KEY_TOKEN,
        ""
    )

    var refreshtoken by Pref(
        PREF_KEY_REFRESH_TOKEN,
        ""
    )

    var accessToken by Pref(
        PREF_KEY_ACCESS_TOKEN,
        ""
    )

    var userId by Pref(
        PREF_KEY_USER_ID,
        ""
    )
    var serverAddress by Pref(
        PREF_KEY_SERVER_ADDRESS,
        "https://api.lamp.digital/"
    )
    var crashValue by Pref(
        PREF_KEY_CRASH_VALUE,
        ""
    )

    var showDisclosureAlert by Pref(
        PREF_SHOW_DISCLOSURE_ALERT,
        true
    )

    var lastAnalyticsTimestamp by Pref(PREF_ANALYTICS_TIME_STAMP, 1L)
    var lastSyncWorkerTimestamp by Pref(PREF_WORKER_TIME_STAMP, 1L)
    var lastSleepDataTimestamp by Pref(PREF_LAST_SLEEP_DATA_TIME_STAMP, 1L)
    var lastStepDataTimestamp by Pref(PREF_LAST_STEP_DATA_TIME_STAMP, 1L)
    var lastGooglefitDataTimestamp by Pref(PREF_GOOGLE_FIT_DATA_TIME_STAMP, 1L)
    var lastSensorDataTimestamp by Pref(PREF_LAST_SENSOR_DATA_TIMESTAMP, 1L)
    var isGoogleHealthConnectConnected by Pref(PREF_GOOGLE_HEALTH_CONNECT_CONNECTED, false)

    var lastSpeedDataTimestamp by Pref(PREF_LAST_SPEED_DATA_TIMESTAMP, 1L)
    var lastTotalCaloriesBurnedDataTimestamp by Pref(PREF_LAST_TOTAL_CALORIES__DATA_TIMESTAMP, 1L)
    var lastDistanceDataTimestamp by Pref(PREF_LAST_DISTANCE_DATA_TIMESTAMP, 1L)
    var lastHydrationTimestamp by Pref(PREF_LAST_HYDRATION_DATA_TIMESTAMP, 1L)
    var lastHeartRateTimestamp by Pref(PREF_LAST_HEART_RATE_DATA_TIMESTAMP, 1L)
    var lastNutritionTimestamp by Pref(PREF_LAST_NUTRITION_DATA_TIMESTAMP, 1L)
    var lastGlucoseTimestamp by Pref(PREF_LAST_BLOOD_GLUCOSE_DATA_TIMESTAMP, 1L)
    var lastPressureTimestamp by Pref(PREF_LAST_BLOOD_PRESSURE_DATA_TIMESTAMP, 1L)
    var lastBodyTemperatureTimestamp by Pref(PREF_LAST_BODY_TEMPERATURE_DATA_TIMESTAMP, 1L)
    var lastStepsCadenceTimestamp by Pref(PREF_LAST_STEPS_CADENCE_DATA_TIMESTAMP, 1L)
    var lastRespiratoryTimestamp by Pref(PREF_LAST_RESPIRATORY_DATA_TIMESTAMP, 1L)
    var currentStreakDays by Pref(PREF_CURRENT_STREAK_DAYS,0)
    var longestStreakDays by Pref(PREF_LONGEST_STREAK_DAYS,0)
    fun clearData() {
        isLoggedIn = false
        token = ""
        refreshtoken = ""
        accessToken = ""
        userId = ""
        serverAddress = ""
        crashValue = ""
        lastAnalyticsTimestamp = 1L
        lastSyncWorkerTimestamp = 1L
        lastSleepDataTimestamp = 1L
        lastGooglefitDataTimestamp = 1L
        lastStepDataTimestamp = 1L
        isCellularUploadAllowed = true
        isLocationPermissionAllowed = false
        isGoogleFitConnected = false
        isTelephonyPermissionAllowed = false
        lastSensorDataTimestamp = 1L
        isGoogleHealthConnectConnected = false
        lastDistanceDataTimestamp = 1L
        lastGlucoseTimestamp = 1L
        lastBodyTemperatureTimestamp = 1L
        lastHydrationTimestamp = 1L
        lastHeartRateTimestamp = 1L
        lastNutritionTimestamp = 1L
        lastPressureTimestamp = 1L
        lastSpeedDataTimestamp = 1L
        lastStepsCadenceTimestamp = 1L
        lastTotalCaloriesBurnedDataTimestamp = 1L
        lastRespiratoryTimestamp = 1L
        currentStreakDays = 0
        longestStreakDays = 0
    }

    var isCellularUploadAllowed by Pref(
        PREF_KEY_ALLOWED_CELLULAR_UPLOAD,
        true
    )

    var isGoogleFitConnected by Pref(
        PREF_GOOGLE_FIT_CONNECTED,
        false
    )

    var isLocationPermissionAllowed by Pref(
        PREF_LOCATION_PERMISSION_ALLOWED,
        false
    )

    var isTelephonyPermissionAllowed by Pref(
        PREF_TELEPHONY_PERMISSION_ALLOWED,
        false
    )
}