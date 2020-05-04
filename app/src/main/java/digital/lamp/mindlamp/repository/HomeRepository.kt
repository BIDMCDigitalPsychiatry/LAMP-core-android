package digital.lamp.mindlamp.repository

import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.NotificationEventRequest
import digital.lamp.mindlamp.network.model.SendTokenRequest
import digital.lamp.mindlamp.network.model.SensorEventRequest
import digital.lamp.mindlamp.network.retrofit.ApiInterface
import digital.lamp.mindlamp.network.retrofit.RetrofitClient

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class HomeRepository {

    private var apiInterface: ApiInterface = RetrofitClient.instance
    private  var apiLogInstance: ApiInterface = RetrofitClient.logInstance

    suspend fun addSensorData(participantId:String, sensorEventRequest: SensorEventRequest) = apiInterface.addSensorEvent(participantId,sensorEventRequest)

    suspend fun addLogData(origin: String, level: String, logEventRequest: LogEventRequest) = apiLogInstance.addLogEvent(origin,level,logEventRequest)

    suspend fun sendTokenData(participantId:String, sendTokenRequest: SendTokenRequest) = apiInterface.sendDeviceToken(participantId,sendTokenRequest)

    suspend fun addNotificationData(participantId:String, notificationEventRequest: NotificationEventRequest) = apiInterface.addNotificationEvent(participantId,notificationEventRequest)

}