package digital.lamp.mindlamp.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NotificationEventRequest (
    @SerializedName("data")
    @Expose
    val notificationData: NotificationData,
    val sensor: String,
    val timestamp: Long
)

data class NotificationData(
    val action: String,
    val user_action: String?,
    val payload: String
)