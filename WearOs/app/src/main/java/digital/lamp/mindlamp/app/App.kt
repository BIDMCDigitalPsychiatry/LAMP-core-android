package digital.lamp.mindlamp.app

/*import android.app.Application
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
}*/

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

import dagger.hilt.android.HiltAndroidApp
import digital.lamp.mindlamp.appstate.Pref
import digital.lamp.mindlamp.sensor.health_services.BackgroundDataSendingWorker
import digital.lamp.mindlamp.sensor.health_services.HealthServiceDataRepository.Companion.PREFERENCES_FILENAME
import digital.lamp.mindlamp.utils.DebugLogs
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Application class, needed to enable dependency injection with Hilt. It also is used to initialize
 * WorkManager.
 */
@HiltAndroidApp
class App : Application(), LifecycleObserver, Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    var inBackground: Boolean = false

    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this


        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        // Initializing Shared pref
        Pref.init(
            this,
            AppKeys.APP_PREF_NAME


        )
        //send data to server periodically
        val sendDataRequest =
            PeriodicWorkRequestBuilder<BackgroundDataSendingWorker>(10L, TimeUnit.MINUTES)
                // Additional configuration
                // .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        WorkManager.getInstance(this)
            .enqueue(sendDataRequest)
        DebugLogs.writeToFile("enqued BackgroundDataSendingWorker")
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

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_FILENAME)

const val TAG = "Passive Data Sample"
