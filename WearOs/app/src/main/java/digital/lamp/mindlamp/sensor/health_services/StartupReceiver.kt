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

package digital.lamp.mindlamp.sensor.health_services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

import dagger.hilt.android.AndroidEntryPoint
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.sensor.workermanager.BackgroundDataSendingWorker
import digital.lamp.mindlamp.sensor.workermanager.HealthServiceDataRegisterWorker
import digital.lamp.mindlamp.service.LampForegroundService
import digital.lamp.mindlamp.utils.DebugLogs
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Background data subscriptions are not persisted across device restarts. This receiver checks if
 * we enabled background data and, if so, registers again.
 */
@AndroidEntryPoint
class StartupReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: HealthServiceDataRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!AppState.session.isLoggedIn) return
        startLampService(context)
        runBlocking {
            // Make sure we have permission.
            val result = context.checkSelfPermission(android.Manifest.permission.BODY_SENSORS)
            if (result == PackageManager.PERMISSION_GRANTED) {
                scheduleWorker(context)
            } else {
                // We may have lost the permission somehow. Mark that background data is
                // disabled so the state is consistent the next time the user opens the app UI.
                repository.setPassiveDataEnabled(false)
            }

        }
    }
    private fun startLampService(context:Context) {
        val serviceIntent = Intent(context, LampForegroundService::class.java).apply {

            putExtra("set_alarm", false)
            putExtra("set_activity_schedule", false)
            putExtra("notification_id", 0)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
    private fun scheduleWorker(context: Context) {
        // BroadcastReceiver's onReceive must complete within 10 seconds. During device startup,
        // sometimes the call to register for background data takes longer than that and our
        // BroadcastReceiver gets destroyed before it completes. Instead we schedule a WorkManager
        // job to perform the registration.
        Log.i("LampWatch", "Enqueuing worker")

       /* WorkManager.getInstance(context).enqueueUniqueWork(
            "healthservicereg",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<HealthServiceDataRegisterWorker>().build()
        )*/
        WorkManager.getInstance(context).enqueue(

            OneTimeWorkRequestBuilder<HealthServiceDataRegisterWorker>().build()
        )
        //send data to server periodically
        val sendDataRequest =
            OneTimeWorkRequestBuilder<BackgroundDataSendingWorker> ()
                // Additional configuration
                //   .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        WorkManager.getInstance(context)
            .enqueue(
                sendDataRequest
            )
    }
}





