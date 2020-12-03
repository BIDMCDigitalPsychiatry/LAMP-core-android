package digital.lamp.mindlamp.network.model

import androidx.room.Ignore

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
data class DimensionData(
    val x: Double?,
    val y: Double?,
    val z: Double?,
    val rotation: RotationData?,
    val motion: MotionData?,
    val gravity: GravityData?,
    val magnetic: MagnetData?,
    val longitude: Double?,
    val latitude: Double?,
    val altitude: Double?,
    val state: Int?,
    val bssid: String?,
    val ssid: String?,
    val steps: Int?,
    val bp_systolic: Float?,
    val bp_diastolic: Float?,
    val unit: String?,
    val value: Any?,
    val activity: ActivityData?
)

data class RotationData(
    val x: Double?,
    val y: Double?,
    val z: Double?
)

data class MotionData(
    val x: Double?,
    val y: Double?,
    val z: Double?
)

data class GravityData(
    val x: Double?,
    val y: Double?,
    val z: Double?
)

data class MagnetData(
    val x: Double?,
    val y: Double?,
    val z: Double?
)

data class ActivityData(
    var running: Boolean?,
    var cycling: Boolean?,
    var automotive: Boolean?,
    var stationary: Boolean?,
    var unknown: Boolean?,
    var walking: Boolean?,
    var on_foot: Boolean?,
) {
    @Ignore
    constructor() : this(null,null,null,null,null,null,null)
}
