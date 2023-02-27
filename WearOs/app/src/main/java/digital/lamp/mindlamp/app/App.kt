package digital.lamp.mindlamp.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.appstate.Pref

import java.io.PrintWriter
import java.io.StringWriter

class App : Application(), LifecycleObserver {

    companion object {
        lateinit var app: App
    }

    var inBackground: Boolean = false
    override fun onCreate() {
        super.onCreate()
        app = this

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        // Initializing Shared pref
        Pref.init(
            this,
            AppKeys.APP_PREF_NAME

        )
        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            handleUncaughtException(
                thread,
                e
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.e("Received Token", "Application going to background")
        inBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.e("Received Token", "Application coming to foreground")
        inBackground = false
    }


    private fun handleUncaughtException(thread: Thread?, e: Throwable) {
        e.printStackTrace() // not all Android versions will print the stack trace automatically

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        val sStackTrace = sw.toString() // stack trace as a string

        AppState.session.crashValue = sStackTrace
    }
}