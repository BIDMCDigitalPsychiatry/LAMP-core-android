package digital.lamp.mindlamp.thread

import android.hardware.Sensor
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

@Suppress("DEPRECATION")
class SendToDataLayerThread // Constructor to send a message to the data layer
    (
    var path: String,
    var message: String,
    var dataMapvals: DataMap,
    var googleApiClient: GoogleApiClient?
) :
    Thread() {

    fun sendMessage() {
        val nodes =
            Wearable.NodeApi.getConnectedNodes(googleApiClient).await()
        for (node in nodes.nodes) {
            val result = Wearable.MessageApi.sendMessage(
                googleApiClient,
                node.id,
                path,
                message.toByteArray()
            ).await()
            if (result.status.isSuccess) {
                Log.v(
                    "myTag",
                    "Message: {" + message + "} sent to: " + node.displayName
                )
            } else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send Message")
            }
        }
    }

    fun syncItems() {

        val putDataMapReq = PutDataMapRequest.create(path)
        // To ensure request is always received in the WearableListenerService
        putDataMapReq.dataMap.putLong("timestamp", System.currentTimeMillis())
        putDataMapReq.dataMap.putAll(dataMapvals)
        val putDataReq = putDataMapReq.asPutDataRequest()
        val pendingResult = Wearable.DataApi.putDataItem(googleApiClient, putDataReq)
        pendingResult.setResultCallback { dataItemResult ->
            if (dataItemResult.status.isSuccess) {
                Log.i(
                    "syncMessage",
                    "SyncMessage successfully sent from caller"
                )
            }
        }
    }

    override fun run() {
        syncItems();
    }

}