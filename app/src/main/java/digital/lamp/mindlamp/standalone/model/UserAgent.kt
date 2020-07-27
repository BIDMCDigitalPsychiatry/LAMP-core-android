package digital.lamp.mindlamp.standalone.model

import android.os.Build

/**
 * Created by ZCO Engineering Dept. on 11,March,2020
 */

data class UserAgent(
    val deviceName: String = Build.MANUFACTURER,
    val model: String = Build.MODEL,
    val os_version: String = Build.VERSION.INCREMENTAL
)