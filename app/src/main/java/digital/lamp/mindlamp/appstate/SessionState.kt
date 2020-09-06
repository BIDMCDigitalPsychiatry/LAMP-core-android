package digital.lamp.mindlamp.appstate

import digital.lamp.mindlamp.BuildConfig

class SessionState {

    companion object {
        const val PREF_KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val PREF_KEY_TOKEN = "token"
        const val PREF_KEY_USER_ID = "user_id"
        const val PREF_KEY_USER_NAME = "user_name"
        const val PREF_KEY_CRASH_VALUE = "crash_value"
        const val DEF_URL = "default_url"
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
    var username by Pref(
        PREF_KEY_USER_NAME,
        ""
    )
    var crashValue by Pref(
        PREF_KEY_CRASH_VALUE,
        ""
    )
    var urlvalue by Pref(
        DEF_URL,
        BuildConfig.HOST
    )

    fun clearData() {
        isLoggedIn = false
        token = ""
        userId = ""
        crashValue = ""
        urlvalue = BuildConfig.HOST
    }
}