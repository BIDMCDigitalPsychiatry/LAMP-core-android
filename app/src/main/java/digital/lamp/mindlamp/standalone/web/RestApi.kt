package digital.lamp.mindlamp.standalone.web

import digital.lamp.mindlamp.standalone.model.SendTokenRequest
import digital.lamp.mindlamp.standalone.model.SensorEventData
import digital.lamp.mindlamp.standalone.web.WebConstant
import digital.lamp.mindlamp.standalone.web.pojo.response.AddDeviceTokenResponse
import digital.lamp.mindlamp.standalone.web.pojo.response.ResponseBase
import digital.lamp.mindlamp.standalone.web.pojo.response.SensorEventResponse
import digital.lamp.mindlamp.standalone.web.pojo.response.UserExistResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RestApi {

    /*@POST("participant/{participant_id}/sensor_event")
    fun addSensorEvent(@Path("participant_id") participantId:String, @Body sensorEventDataList: ArrayList<SensorEventData>) : Response<ResponseBody>
*/
    @POST(WebConstant.ADD_SENSOR_DATA)
    fun sendDeviceToken(@Path("participant_id") participantId:String, @Body sendTokenRequest: SendTokenRequest) : Call<AddDeviceTokenResponse>

    @POST(WebConstant.ADD_SENSOR_DATA)
    fun addSensorEvent(@Path("participant_id") participantId:String, @Body sensorEventDataList: ArrayList<SensorEventData>): Call<SensorEventResponse?>?

    @GET(WebConstant.PARTICIPANT_ID)
    fun isUserExists(@Path("participant_id") participantId:String): Call<UserExistResponse?>?
}