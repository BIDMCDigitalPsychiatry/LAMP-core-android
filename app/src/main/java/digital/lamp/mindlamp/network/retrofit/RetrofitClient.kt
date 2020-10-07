package digital.lamp.mindlamp.network.retrofit

import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
object RetrofitClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit. SECONDS)
        .addInterceptor(loggingInterceptor())
        .addInterceptor(authorizationInterceptor())
        .build()

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }
        else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return interceptor
    }

    private fun authorizationInterceptor(): Interceptor {
        return Interceptor { chain ->
            val requestOriginal = chain.request()

            val request = requestOriginal.newBuilder()
                .addHeader("Authorization", "Basic ${Utils.toBase64(
                    AppState.session.token+":"+AppState.session.serverAddress.removePrefix("https://").removePrefix("http://")).toString().trim()}")
                .build()

            chain.proceed(request)
        }
    }

    val instance: ApiInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppState.session.serverAddress)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiInterface::class.java)
    }

    val logInstance: ApiInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_LOG)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiInterface::class.java)
    }
}