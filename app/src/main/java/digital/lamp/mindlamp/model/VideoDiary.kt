package digital.lamp.mindlamp.model

import com.google.gson.annotations.SerializedName

data class SessionConfig(
    @SerializedName("settings")
    val settings: Settings?,

    @SerializedName("activityId")
    val activityId: String?,

    @SerializedName("participantId")
    val participantId: String?,

    @SerializedName("activityName")
    val activityName : String?
)

data class Settings(
    @SerializedName("maxDurationInSec")
    val maxDurationInSec: Int?,

    @SerializedName("resolution")
    val resolution: Int?,

    @SerializedName("maxBitrateMbps")
    val maxBitrateMbps: Int?,

    @SerializedName("frameRate")
    val frameRate: Int?,

    @SerializedName("metadataCapture")
    val metadataCapture: Boolean?
)
