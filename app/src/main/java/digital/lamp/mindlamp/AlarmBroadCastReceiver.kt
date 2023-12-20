package digital.lamp.mindlamp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils.isServiceRunning

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 * This class is responsible for start foreground service if the device reboot action completed
 */
class AlarmBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val action: String? = intent.action
            val id = intent.getIntExtra("id", 0)

            if (Intent.ACTION_BOOT_COMPLETED == action) {
                LampLog.e("Receiver Triggered From Boot")
                //Start Service Directly
                if (AppState.session.isLoggedIn) {
                    val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                        putExtra("inputExtra", "Foreground Service Example in Android")
                        putExtra("set_alarm", false)
                        putExtra("set_activity_schedule", false)
                        putExtra("notification_id", 0)

                    }
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            } else {
                LampLog.e("Receiver Triggered :: $id ")
                if (AppState.session.isLoggedIn && context.isServiceRunning(LampForegroundService::class.java)) {
                    val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                        putExtra("inputExtra", "Foreground Service Example in Android")
                        putExtra("set_alarm", true)
                        putExtra("set_activity_schedule", false)
                        putExtra("notification_id", 0)
                    }
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else if (AppState.session.isLoggedIn && !context.isServiceRunning(
                        LampForegroundService::class.java
                    )
                ) {
                    val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                        putExtra("inputExtra", "Foreground Service Example in Android")
                        putExtra("set_alarm", false)
                        putExtra("set_activity_schedule", false)
                        putExtra("notification_id", 0)
                    }
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}