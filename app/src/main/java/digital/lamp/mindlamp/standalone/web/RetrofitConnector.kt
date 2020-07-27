package digital.lamp.mindlamp.standalone.web

import digital.lamp.mindlamp.standalone.BuildConfig
import digital.lamp.mindlamp.standalone.appstate.AppState
import digital.lamp.mindlamp.standalone.utils.Utils
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
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(WebConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(WebConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS)
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader(
                            "Authorization",
                            "Basic ${AppState.session.userId
                                .trim()}"
                        )
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .build()
            return Retrofit.Builder()
                .baseUrl(BuildConfig.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }

    /**
     * method to call connector with header
     *
     * @return
     */
    val retrofitWithHeader: Retrofit
        get() {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(WebConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(WebConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS)
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        /*.addHeader(
                            WebConstant.SESSION_TOKEN,
                            WebConstant.BEARER *//*+ PrefernceManager.getInstance().getUserDetails()
                                .getSessionToken()*//*
                        )*/
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .build()
            return Retrofit.Builder()
                .baseUrl(BuildConfig.HOST)
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