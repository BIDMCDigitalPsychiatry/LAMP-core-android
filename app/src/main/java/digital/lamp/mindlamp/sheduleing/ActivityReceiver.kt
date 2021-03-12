package digital.lamp.mindlamp.sheduleing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.repository.LampForegroundService
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils.isServiceRunning

/**
 * Created by ZCO Engineering Dept. on 11,March,2021
 */
class ActivityReceiver : BroadcastReceiver() {
    companion object{
        private val TAG = ActivityReceiver::class.java.simpleName
    }
    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra("id",0)
        LampLog.e(TAG,"Receiver Fired :: $id")
        val action: String? = intent.action
        if(Intent.ACTION_BOOT_COMPLETED == action){
            LampLog.e("Receiver Triggered From Boot")
        }
        else {
            if (AppState.session.isLoggedIn && context.isServiceRunning(LampForegroundService::class.java)) {
                    //Perform action for alarm trigger for firing local notification
                    val serviceIntent = Intent(context, LampForegroundService::class.java).apply {
                        putExtra("inputExtra", "Foreground Service Example in Android")
                        putExtra("set_alarm", false)
                        putExtra("set_activity_schedule", true)
                        putExtra("notification_id",id)
                    }
                    ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}