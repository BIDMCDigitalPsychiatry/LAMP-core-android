package digital.lamp.mindlamp.repository

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.*
import digital.lamp.mindlamp.model.CustomMap


class MobileListenerService : WearableListenerService() {

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


        } else {
            super.onMessageReceived(messageEvent)
        }
    }

    fun sendBroadcast(key: String, arrStringArray: Array<String>) {
        // Broadcast message to wearable activity for display
        // Broadcast message to wearable activity for display
        val messageIntent = Intent()
        messageIntent.action = Intent.ACTION_SEND
        messageIntent.putExtra(key, arrStringArray)
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)

    }

    /* fun fromDataMap(dataMap: DataMap): HashMap<String, Array<String>> {
         val hashMap = HashMap<String, Array<String>>()
         for (key in dataMap.keySet()) {
             if (null != dataMap.getStringArray(key))
                 hashMap[key] = dataMap.getStringArray(key)
             else
                 hashMap[key] = dataMap.getString(key)
         }
         return hashMap
     }*/

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.i("service", "Event received in MobileListenerService")
        for (dataEvent in dataEventBuffer) {
            if (dataEvent.type == DataEvent.TYPE_CHANGED) {
                val item = dataEvent.dataItem
                if (item.uri.path == "/updatedValues") {
                    var dataMap = DataMapItem.fromDataItem(item).dataMap

                    // Broadcast message to wearable activity for display
                    val messageIntent = Intent()
                    messageIntent.action = Intent.ACTION_SEND
                    messageIntent.putExtra("sensorvalues", CustomMap(dataMap))
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)


                } else if (item.uri.path == "/getSensorVals") {
                    /* val dataMap = DataMapItem.fromDataItem(item).dataMap
                     val sensors: Array<String> = dataMap.get("reqeustedsensors") as Array<String>

                     sendBroadcast("values", sensors)*/
                }
            }
        }
        super.onDataChanged(dataEventBuffer)
    }
}