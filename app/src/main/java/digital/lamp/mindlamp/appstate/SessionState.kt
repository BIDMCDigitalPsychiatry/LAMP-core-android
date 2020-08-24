package digital.lamp.mindlamp.appstate

class SessionState {

    companion object {
        const val PREF_KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val PREF_KEY_TOKEN = "token"
        const val PREF_KEY_USER_ID = "user_id"
        const val PREF_KEY_CRASH_VALUE = "crash_value"
        const val CURRENT_VERSION = "currentversion"
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
    var currentversion by Pref(
        CURRENT_VERSION,
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