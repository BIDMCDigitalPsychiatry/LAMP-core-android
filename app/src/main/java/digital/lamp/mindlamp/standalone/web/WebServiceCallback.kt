package digital.lamp.mindlamp.standalone.web

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import digital.lamp.mindlamp.standalone.web.pojo.response.ResponseBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebServiceCallback<T> internal constructor(
    requestCode: Int,
    webserviceResponseLiveData: LiveData<WebServiceResponseData>
) :
    Callback<T> {
    private val mWebserviceResponseLiveData: LiveData<WebServiceResponseData>
    private val mWebServiceResponseData: WebServiceResponseData =
        WebServiceResponseData()

    override fun onResponse(@NonNull responseBaseCall: Call<T>?, @NonNull response: Response<T>) {
        mWebServiceResponseData.responseCode = response.code()
        mWebServiceResponseData.isSuccessful = response.isSuccessful()
        mWebServiceResponseData.responseBase = response.body() as ResponseBase
        mWebServiceResponseData.responseMessage = response.message()
        (mWebserviceResponseLiveData as MutableLiveData<WebServiceResponseData?>).postValue(
            mWebServiceResponseData
        )
    }

    override fun onFailure(@NonNull responseBaseCall: Call<T>?, @NonNull t: Throwable?) {
        if (t is java.net.SocketTimeoutException) {
            mWebServiceResponseData.responseCode = WebConstant.CODE_SOCKET_TIME_OUT
        }
        mWebServiceResponseData.isSuccessful = false
        (mWebserviceResponseLiveData as MutableLiveData<WebServiceResponseData?>).postValue(
            mWebServiceResponseData
        )
    }

    init {
        mWebserviceResponseLiveData = webserviceResponseLiveData
        mWebServiceResponseData.requestCode = requestCode
    }
}