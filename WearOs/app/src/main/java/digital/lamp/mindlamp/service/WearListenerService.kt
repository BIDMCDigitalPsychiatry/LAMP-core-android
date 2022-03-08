package digital.lamp.mindlamp.service

import android.content.Intent
import android.hardware.Sensor
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.*

class WearListenerService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
    }
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/message_path") {
            val message = String(messageEvent.data)
            Log.v(
                "myTag",
                "Message path received on watch is: " + messageEvent.path
            )
            Log.v("myTag", "Message received on watch is: $message")

            // Broadcast message to wearable activity for display
            val messageIntent = Intent()
            messageIntent.action = Intent.ACTION_SEND
            messageIntent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
        } else {
            super.onMessageReceived(messageEvent)
        }
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.i("service", "Event received in WearableListenerService")
        for (dataEvent in dataEventBuffer) {
            if (dataEvent.type == DataEvent.TYPE_CHANGED) {
                val item = dataEvent.dataItem
                if (item.uri.path == "/updateItems") {
//                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    /* val dataMapItems =
                         dataMap.getDataMapArrayList("timestamp")*/

                } else if (item.uri.path == "/getSensorVals") {

                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    val requestedSensors: Array<String> =
                        dataMap.get("reqeustedsensors") as Array<String>
                    Log.v("myTag", "Message received on watch is:")
                    // Broadcast message to wearable activity for display
                    val messageIntent = Intent()
                    messageIntent.action = Intent.ACTION_SEND
                    messageIntent.putExtra("values", requestedSensors)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)

                }
            }
        }
        super.onDataChanged(dataEventBuffer)
    }

    override fun onPeerDisconnected(node: Node?) {
        super.onPeerDisconnected(node)
    }
}