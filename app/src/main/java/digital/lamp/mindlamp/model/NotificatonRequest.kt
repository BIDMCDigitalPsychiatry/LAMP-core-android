package digital.lamp.mindlamp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificatonRequest(

    @SerializedName("data")
    @Expose
    val notdata: NotificationData,
    val timestamp: Long = 0,
    val sensor: String = ""


)

data class NotificationData(
    val action: String = "",
    val content: String = ""
)