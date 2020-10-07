package digital.lamp.mindlamp.network.model

import android.os.Build
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.appstate.AppState

/**
 * Created by ZCO Engineering Dept. on 11,March,2020
 */
data class LogEventRequest(
    var message: String,
    val user_id: String,
    val user_agent: String
) {
    constructor() : this("", AppState.session.userId,BuildConfig.VERSION_NAME+","+Build.VERSION.INCREMENTAL+","+Build.MANUFACTURER+","+Build.MODEL)
}