package digital.lamp.mindlamp.standalone.viewmodels.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import digital.lamp.mindlamp.standalone.model.SendTokenRequest
import digital.lamp.mindlamp.standalone.model.SensorEventData
import digital.lamp.mindlamp.standalone.web.WebServiceManager
import digital.lamp.mindlamp.standalone.web.WebServiceResponseData

class WebServiceRepository {

    private val webServiceResponseLiveData: LiveData<WebServiceResponseData> =
        MutableLiveData()

    private fun WebServiceRepository() {}

    companion object {
        private var sInstance: WebServiceRepository? = null
        fun getInstance(): WebServiceRepository? {
            sInstance =
                WebServiceRepository()
            return sInstance
        }
    }

    fun getWebServiceResponseLiveData(): LiveData<WebServiceResponseData>? {
        return webServiceResponseLiveData
    }

    fun callUpdateSensordataWS(
        participantId: String, lstSensorEventData: ArrayList<SensorEventData>,
        webServiceResponseDataLiveData: LiveData<WebServiceResponseData>
    ) {
        WebServiceManager.instance?.addSensorData(
            participantId, lstSensorEventData,
            webServiceResponseDataLiveData
        )
    }

    fun callAddDeviceTokenWS(
        participantId: String, sendTokenRequest: SendTokenRequest,
        webServiceResponseDataLiveData: LiveData<WebServiceResponseData>
    ) {
        WebServiceManager.instance?.adddeviceToken(
            participantId, sendTokenRequest,
            webServiceResponseDataLiveData
        )
    }

    fun callIsuserExistsWS(
        participantId: String, webServiceResponseDataLiveData: LiveData<WebServiceResponseData>
    ) {
        WebServiceManager.instance?.isUserExists(
            participantId, webServiceResponseDataLiveData
        )
    }

}