package digital.lamp.mindlamp.standalone.web

import digital.lamp.mindlamp.standalone.web.RestApi
import android.util.Log
import androidx.lifecycle.LiveData
import digital.lamp.mindlamp.standalone.model.SendTokenRequest
import digital.lamp.mindlamp.standalone.model.SensorEventData

class WebServiceManager {

    /**
     * methods to add sensor data
     * @param participantId Id
     * @param webserviceResponseLiveData
     * @return
     */
    fun addSensorData(
        participantId: String, lstSensoerdata : ArrayList<SensorEventData>,
        webserviceResponseLiveData: LiveData<WebServiceResponseData>
    ) {
        val service: RestApi? = RetrofitConnector.instance?.retrofit?.create(RestApi::class.java)
        try {

            service?.addSensorEvent(participantId, lstSensoerdata)!!.enqueue(
                WebServiceCallback(
                    WebConstant.ADDSENSOREVENT_REQ_CODE,
                    webserviceResponseLiveData
                )
            )
        } catch (ignored: java.lang.Exception) {

            Log.d("exception", ignored.message)
        }
    }

    /**
     * methods to add device token
     * @param participantId Id
     * @param sendTokenRequest
     * @param webserviceResponseLiveData
     * @return
     */
    fun adddeviceToken(
        participantId: String, sendTokenRequest: SendTokenRequest,
        webserviceResponseLiveData: LiveData<WebServiceResponseData>
    ) {
        val service: RestApi? = RetrofitConnector.instance?.retrofit?.create(RestApi::class.java)
        try {

            service?.sendDeviceToken(participantId, sendTokenRequest)!!.enqueue(
                WebServiceCallback(
                    WebConstant.ADDDEVICETOKEN_REQ_CODE,
                    webserviceResponseLiveData
                )
            )
        } catch (ignored: java.lang.Exception) {

            Log.d("exception", ignored.message)
        }
    }

    /**
     * methods to add sensor data
     * @param participantId Id
     * @param webserviceResponseLiveData
     * @return
     */
    fun isUserExists(
        participantId: String,
        webserviceResponseLiveData: LiveData<WebServiceResponseData>
    ) {
        val service: RestApi? = RetrofitConnector.instance?.retrofit?.create(RestApi::class.java)
        try {

            service?.isUserExists(participantId)!!.enqueue(
                WebServiceCallback(
                    WebConstant.ISUSEREXISTS_REQ_CODE,
                    webserviceResponseLiveData
                )
            )
        } catch (ignored: java.lang.Exception) {

            Log.d("exception", ignored.message)
        }
    }

    companion object {
        private var mWebServiceManager: WebServiceManager? = null
        val instance: WebServiceManager?
            get() {
                if (mWebServiceManager == null) {
                    mWebServiceManager =
                        WebServiceManager()
                }
                return mWebServiceManager
            }
    }
}