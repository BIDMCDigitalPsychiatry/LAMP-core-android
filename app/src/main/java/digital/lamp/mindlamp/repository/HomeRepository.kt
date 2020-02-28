package digital.lamp.mindlamp.repository

import digital.lamp.mindlamp.network.model.SensorEventRequest
import digital.lamp.mindlamp.network.retrofit.ApiInterface
import digital.lamp.mindlamp.network.retrofit.RetrofitClient

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class HomeRepository {

    private var apiInterface: ApiInterface = RetrofitClient.instance

    suspend fun addSensorData(participantId:String, sensorEventRequest: SensorEventRequest) = apiInterface.addSensorEvent(participantId,sensorEventRequest)

}