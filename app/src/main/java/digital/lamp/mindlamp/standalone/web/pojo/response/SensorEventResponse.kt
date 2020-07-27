package digital.lamp.mindlamp.standalone.web.pojo.response

import com.google.gson.annotations.SerializedName
import digital.lamp.mindlamp.standalone.web.pojo.UserExistDetails

class SensorEventResponse : ResponseBase() {

    @SerializedName("data")
    lateinit var dt: Any
}