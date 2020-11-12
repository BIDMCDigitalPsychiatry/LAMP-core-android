package digital.lamp.mindlamp.network.model

import android.os.Build
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import digital.lamp.mindlamp.BuildConfig

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
    var action: String,
    var device_token: String?,
    var device_type: String,
    val user_agent: String
    ){
    constructor() : this("",null,"",BuildConfig.VERSION_NAME+","+Build.VERSION.INCREMENTAL+","+Build.MANUFACTURER+","+Build.MODEL)
}
