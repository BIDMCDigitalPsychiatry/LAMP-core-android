package digital.lamp.mindlamp.viewmodels

import android.app.Application
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.SendTokenRequest
import digital.lamp.mindlamp.viewmodels.repositories.WebServiceRepository
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData

class DataViewModel(application: Application) : BaseViewModel(application) {

    private var webServiceRepository = WebServiceRepository()

    fun addSensoreEvent(participantId: String, lstSensorEventData: ArrayList<SensorEventData>) {
        webServiceRepository.callUpdateSensordataWS(
            participantId, lstSensorEventData,
            getWebServiceResponseLiveData()
        )
    }

    fun addDeviceToken(participantId: String, sendTokenRequest: SendTokenRequest) {
        webServiceRepository.callAddDeviceTokenWS(
            participantId, sendTokenRequest,
            getWebServiceResponseLiveData()
        )
    }

    fun addLogEvent(origin: String, level:String, logEventRequest: LogEventRequest) {
        webServiceRepository.callLogEventnWS(
            origin, level, logEventRequest,
            getWebServiceResponseLiveData()
        )
    }

    fun isUserExists(participantId: String) {
        webServiceRepository.callIsuserExistsWS(
            participantId,
            getWebServiceResponseLiveData()
        )
    }
}