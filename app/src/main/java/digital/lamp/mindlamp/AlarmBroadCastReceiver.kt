package digital.lamp.mindlamp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.utils.Utils.isServiceRunning

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class AlarmBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.action
        if(Intent.ACTION_BOOT_COMPLETED == action){
            LampLog.e("Receiver Triggered From Boot")
            //Start Service Directly
            if(AppState.session.isLoggedIn) {
                val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                    putExtra("inputExtra", "Foreground Service Example in Android")
                    putExtra("set_alarm", false)

                }
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }else{
            LampLog.e("Receiver Triggered ")
            if(AppState.session.isLoggedIn && context.isServiceRunning(LampForegroundService::class.java)) {
                val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                    putExtra("inputExtra", "Foreground Service Example in Android")
                    putExtra("set_alarm", true)
                }
                ContextCompat.startForegroundService(context, serviceIntent)
            } else if(AppState.session.isLoggedIn && !context.isServiceRunning(LampForegroundService::class.java)){
                val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                    putExtra("inputExtra", "Foreground Service Example in Android")
                    putExtra("set_alarm", false)
                }
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}