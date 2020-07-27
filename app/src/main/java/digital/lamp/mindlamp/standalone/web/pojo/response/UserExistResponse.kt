package digital.lamp.mindlamp.standalone.web.pojo.response

import com.google.gson.annotations.SerializedName
import digital.lamp.mindlamp.standalone.web.pojo.UserExistDetails

class UserExistResponse : ResponseBase() {

    @SerializedName("data")
    var lstuserdetails: ArrayList<UserExistDetails> = ArrayList<UserExistDetails>()
}