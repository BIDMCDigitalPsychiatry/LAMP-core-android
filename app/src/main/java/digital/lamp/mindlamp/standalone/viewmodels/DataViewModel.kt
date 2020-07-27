package digital.lamp.mindlamp.standalone.viewmodels

import android.app.Application
import digital.lamp.mindlamp.standalone.model.SendTokenRequest
import digital.lamp.mindlamp.standalone.model.SensorEventData
import digital.lamp.mindlamp.standalone.viewmodels.repositories.WebServiceRepository

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

    fun isUserExists(participantId: String) {
        webServiceRepository.callIsuserExistsWS(
            participantId,
            getWebServiceResponseLiveData()
        )
    }
}