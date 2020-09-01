package digital.lamp.mindlamp.web.pojo.response

import com.google.gson.annotations.SerializedName

class AddDeviceTokenResponse : ResponseBase() {

    @SerializedName("data")
    lateinit var dt: Any
}