package digital.lamp.mindlamp.appstate

class SessionState {

    companion object {
        const val PREF_KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val PREF_KEY_TOKEN = "token"
        const val PREF_KEY_USER_ID = "user_id"
        const val PREF_KEY_CRASH_VALUE = "crash_value"
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
    var crashValue by Pref(
        PREF_KEY_CRASH_VALUE,
        ""
    )
    fun clearData() {
        isLoggedIn = false
        token = ""
        userId = ""
        crashValue = ""
    }
}