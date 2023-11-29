package digital.lamp.mindlamp.model

import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.Utils


/**
 * Data class representing a log event request.
 *Created by ZCO Engineering Dept. on 11,March,2020
 * @param message The log message.
 * @param user_id The user ID associated with the log event.
 * @param user_agent The user agent information.
 * @param device_type The type of the device (e.g., "Android").
 */
data class LogEventRequest(
    var message: String,
    val user_id: String,
    val user_agent: String,
    val device_type: String
) {
    constructor() : this("", AppState.session.userId, Utils.getUserAgent(), "Android")
}
