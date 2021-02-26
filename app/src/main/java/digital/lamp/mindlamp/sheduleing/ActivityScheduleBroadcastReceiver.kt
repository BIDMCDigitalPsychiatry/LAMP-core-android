package digital.lamp.mindlamp.sheduleing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import digital.lamp.mindlamp.utils.LampLog

/**
 * Created by ZCO Engineering Dept. on 24,February,2021
 */
class ActivityScheduleBroadcastReceiver : BroadcastReceiver() {
    companion object{
        private val TAG = ActivityScheduleBroadcastReceiver::class.java.simpleName
    }
    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra("id",0)
        LampLog.e(TAG,"Receiver Fired :: $id")
        val action: String? = intent.action
        if(Intent.ACTION_BOOT_COMPLETED == action){
            LampLog.e("Receiver Triggered From Boot")
        }
    }
}