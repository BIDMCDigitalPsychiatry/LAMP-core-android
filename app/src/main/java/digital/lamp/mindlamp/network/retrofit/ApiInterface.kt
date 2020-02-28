package digital.lamp.mindlamp.network.retrofit

import digital.lamp.mindlamp.network.model.SensorEventRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface ApiInterface {

    @POST("participant/{participant_id}/sensor_event")
    suspend fun addSensorEvent(@Path("participant_id") participantId:String, @Body sensorEventRequest: SensorEventRequest) : ResponseBody
}