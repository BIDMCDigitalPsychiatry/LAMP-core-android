package digital.lamp.mindlamp.web.pojo.response

import com.google.gson.annotations.SerializedName
import digital.lamp.mindlamp.web.pojo.UserExistDetails

class SensorEventResponse : ResponseBase() {

    @SerializedName("data")
    lateinit var dt: Any
}