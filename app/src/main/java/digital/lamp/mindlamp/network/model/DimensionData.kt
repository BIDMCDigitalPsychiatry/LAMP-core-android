package digital.lamp.mindlamp.network.model

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
    val unit: String?,
    val value: Int?
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
