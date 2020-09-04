package digital.lamp.mindlamp.model

import android.os.Build
import digital.lamp.mindlamp.BuildConfig

/**
 * Created by ZCO Engineering Dept. on 11,March,2020
 */
data class LogEventRequest(
    val message: String,
    val user_agent: UserAgent? =null,
    val user_id: String
)

data class UserAgent(
    val deviceName: String = Build.MANUFACTURER,
    val model: String = Build.MODEL,
    val os_version: String = Build.VERSION.INCREMENTAL,
    val app_version: String = BuildConfig.VERSION_NAME
)