package digital.lamp.mindlamp.standalone.web.pojo.response

import com.google.gson.annotations.SerializedName

class AddDeviceTokenResponse : ResponseBase() {

    @SerializedName("data")
    lateinit var dt: Any
}