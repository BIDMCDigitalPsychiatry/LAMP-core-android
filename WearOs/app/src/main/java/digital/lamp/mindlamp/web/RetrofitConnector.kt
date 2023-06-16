package digital.lamp.mindlamp.web

import digital.lamp.mindlamp.appstate.AppState
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConnector private constructor() {

    /**
     * method to call connector with header
     *
     * @return
     */
    val retrofit: Retrofit
        get() {

            var usid = ""
            if (WebConstant.USERID.isEmpty())
                usid = AppState.session.userId
            else
                usid = WebConstant.USERID

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(WebConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(WebConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS)
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader(
                            "Authorization",
                            "Basic ${usid.trim()}"
                        )
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .build()

            var url = AppState.session.urlvalue
            if (!AppState.session.urlvalue.startsWith("https://")) {
                url = "https://" + AppState.session.urlvalue
            }
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }

    companion object {
        private var mRetrofitConnector: RetrofitConnector? = null
        val instance: RetrofitConnector?
            get() {
                if (mRetrofitConnector == null) {
                    mRetrofitConnector = RetrofitConnector()
                }
                return mRetrofitConnector
            }
    }
}