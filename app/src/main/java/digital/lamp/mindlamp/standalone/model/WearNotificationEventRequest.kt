package digital.lamp.mindlamp.standalone.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WearNotificationEventRequest (
    @SerializedName("data")
    @Expose
    val notificationData: WearNotificationData,
    val sensor: String,
    val timestamp: Long
)

data class WearNotificationData(
    val action: String,
    val user_action: String?,
    val payload: String
)