package digital.lamp.mindlamp.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

private const val TAG = "SensorBroadcastReceiver"

class SensorBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val messageIntent = Intent()
        messageIntent.action = Intent.ACTION_SEND
        messageIntent.putExtra("title", intent.getStringExtra("title"))
        messageIntent.putExtra("action", intent.getStringExtra("action"))
        messageIntent.putExtra("content", intent.getStringExtra("content"))
        LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent)

    }
}
