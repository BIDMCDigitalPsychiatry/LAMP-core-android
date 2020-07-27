package digital.lamp.mindlamp.standalone.web.pojo.response

import com.google.gson.annotations.SerializedName

abstract class ResponseBase {

    @SerializedName("Details")
    var details: String? = null

    @SerializedName("Message")
    var errorMessage: String? = null

    @SerializedName("ErrorCode")
    var errorCode = 0

}