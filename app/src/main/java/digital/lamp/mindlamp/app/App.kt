package digital.lamp.mindlamp.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ApiClient
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.appstate.Pref
import digital.lamp.mindlamp.auth.TokenAuthenticator
import digital.lamp.mindlamp.utils.AppLifeCycleListener
import digital.lamp.mindlamp.utils.DebugLogs
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private val lifecycleListener: AppLifeCycleListener by lazy {
        AppLifeCycleListener()
    }

  /*  val healthConnectManager by lazy {
        GoogleHealthConnect(this)
    }*/
    companion object {
        lateinit var app: App
    }
    override fun onCreate() {
        super.onCreate()
        app = this


     //ANRWatchDog().start()
      /*  StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
               .penaltyDeath()
                .build())*/
        // Initializing Shared pref
        val directBootContext: Context = this.createDeviceProtectedStorageContext()

        Pref.init(
            directBootContext,
            AppKeys.APP_PREF_NAME
        )
        // Register the Bearer-token refresh handler before any LAMP API client
        // is built (the OkHttp client is lazily created from ApiClient.builder).
        ApiClient.builder.authenticator(TokenAuthenticator())
        // Setup handler for uncaught exceptions.
       /* Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            handleUncaughtException(
                thread,
                e
            )
        } */
      ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleListener)

    }
    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setMinimumLoggingLevel(Log.VERBOSE)
                     .setWorkerFactory(workerFactory)
                    .build()

    private fun handleUncaughtException(thread: Thread?, e: Throwable) {
        e.printStackTrace() // not all Android versions will print the stack trace automatically

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        val sStackTrace = sw.toString() // stack trace as a string

        DebugLogs.writeToFile("Crash : $sStackTrace")
        AppState.session.crashValue = sStackTrace
    }


    fun isApplicationInForeground():Boolean{
        return lifecycleListener.isOnForground
    }
}