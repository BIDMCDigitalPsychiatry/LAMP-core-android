package digital.lamp.mindlamp.web

import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.SendTokenRequest
import digital.lamp.mindlamp.web.pojo.response.AddDeviceTokenResponse
import digital.lamp.mindlamp.web.pojo.response.SensorEventResponse
import digital.lamp.mindlamp.web.pojo.response.UserExistResponse
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RestApi {

    /*@POST("participant/{participant_id}/sensor_event")
    fun addSensorEvent(@Path("participant_id") participantId:String, @Body sensorEventDataList: ArrayList<SensorEventData>) : Response<ResponseBody>
*/
    @PUT(WebConstant.ADD_LOG_DATA)
    fun addLogEvent(@Query("origin") origin:String, @Query("level") level:String, @Body logEventRequest: LogEventRequest) : Call<ResponseBody>

    @POST(WebConstant.ADD_SENSOR_DATA)
    fun sendDeviceToken(@Path("participant_id") participantId:String, @Body sendTokenRequest: SendTokenRequest) : Call<AddDeviceTokenResponse>

    @POST(WebConstant.ADD_SENSOR_DATA)
    fun addSensorEvent(@Path("participant_id") participantId:String, @Body sensorEventDataList: ArrayList<SensorEventData>): Call<SensorEventResponse?>?

    @GET(WebConstant.PARTICIPANT_ID)
    fun isUserExists(@Path("participant_id") participantId:String): Call<UserExistResponse?>?
}