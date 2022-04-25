package digital.lamp.mindlamp.appstate

class SessionState {

    companion object {
        const val PREF_KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val PREF_KEY_TOKEN = "token"
        const val PREF_KEY_USER_ID = "user_id"
        const val PREF_KEY_SERVER_ADDRESS= "serverAddress"
        const val PREF_KEY_CRASH_VALUE = "crash_value"
        const val PREF_ANALYTICS_TIME_STAMP = "lastAnalyticsTimestamp"
        const val PREF_WORKER_TIME_STAMP = "lastSyncWorkerTimestamp"
        const val PREF_SHOW_DISCLOSURE_ALERT = "show_disclosure_alert"
        const val PREF_KEY_ALLOWED_CELLULAR_UPLOAD ="allowed_cellular_upload"
        const val PREF_LAST_SLEEP_DATA_TIME_STAMP = "last_sleep_data_timestamp"
        const val PREF_LAST_STEP_DATA_TIME_STAMP = "last_step_data_timestamp"
        const val PREF_GOOGLE_FIT_CONNECTED = "google_fit_connected"
    }

    var isLoggedIn by Pref(
        PREF_KEY_IS_USER_LOGGED_IN,
        false
    )
    var token by Pref(
        PREF_KEY_TOKEN,
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

    var lastAnalyticsTimestamp by Pref(PREF_ANALYTICS_TIME_STAMP,1L)
    var lastSyncWorkerTimestamp by Pref(PREF_WORKER_TIME_STAMP,1L)
    var lastSleepDataTimestamp by Pref(PREF_LAST_SLEEP_DATA_TIME_STAMP,1L)
    var lastStepDataTimestamp by Pref(PREF_LAST_STEP_DATA_TIME_STAMP,1L)

    fun clearData() {
        isLoggedIn = false
        token = ""
        userId = ""
        serverAddress = ""
        crashValue = ""
        lastAnalyticsTimestamp = 1L
        lastSyncWorkerTimestamp = 1L
        lastSleepDataTimestamp =1L
        isCellularUploadAllowed = true
    }

    var isCellularUploadAllowed by Pref(
        PREF_KEY_ALLOWED_CELLULAR_UPLOAD,
        true
    )

    var isGoogleFitConnected by Pref(
            PREF_GOOGLE_FIT_CONNECTED,
            true
    )
}