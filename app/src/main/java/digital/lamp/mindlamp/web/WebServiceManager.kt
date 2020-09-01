package digital.lamp.mindlamp.web

import android.util.Log
import androidx.lifecycle.LiveData
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.SendTokenRequest
import digital.lamp.mindlamp.web.RetrofitConnector
import digital.lamp.mindlamp.web.WebServiceResponseData
import digital.lamp.mindlamp.web.RestApi
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData

class WebServiceManager {

    /**
     * methods to add sensor data
     * @param participantId Id
     * @param webserviceResponseLiveData
     * @return
     */
    fun addSensorData(
        participantId: String, lstSensoerdata: ArrayList<SensorEventData>,
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
     * methods to add log
     * @param origin
     * @param level
     * @param webserviceResponseLiveData
     * @return
     */
    fun addLogEvent(
        origin: String, level: String, logEventRequest: LogEventRequest,
        webserviceResponseLiveData: LiveData<WebServiceResponseData>
    ) {
        val service: RestApi? = RetrofitConnector.instance?.retrofit?.create(RestApi::class.java)
        try {

            service?.addLogEvent(origin, level, logEventRequest)!!.enqueue(
                WebServiceCallback(
                    WebConstant.LOGEVENT_REQ_CODE,
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