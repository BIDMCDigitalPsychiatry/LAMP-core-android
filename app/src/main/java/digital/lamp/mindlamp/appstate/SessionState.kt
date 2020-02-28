package digital.lamp.mindlamp.appstate

class SessionState {

    companion object {
        const val PREF_KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val PREF_KEY_TOKEN = "token"
        const val PREF_KEY_USER_ID = "user_id"
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

    fun clearData() {
        isLoggedIn = false
        token = ""
        userId = ""
    }
}