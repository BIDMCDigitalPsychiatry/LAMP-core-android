package digital.lamp.mindlamp.model

import android.os.Build
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.appstate.AppState

/**
 * Created by ZCO Engineering Dept. on 11,March,2020
 */
data class LogEventRequest(
    var message: String,
    val user_id: String,
    val user_agent: String,
    val device_type: String
) {
    constructor() : this("", AppState.session.userId,"NativeCore "+BuildConfig.VERSION_NAME+";"+Build.MANUFACTURER+";"+Build.MODEL,"Android")
}
