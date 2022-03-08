package digital.lamp.mindlamp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by ZCO Engineering Dept. on 23,April,2020
 */
data class SendTokenRequest(
    @SerializedName("data")
    @Expose
    val tokenData: TokenData,
    val sensor: String,
    val timestamp: Long
)

data class TokenData(
    val action: String,
    val device_token: String,
    val device_type: String,
    val user_agent: UserAgent? =null
    )
