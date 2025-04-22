package digital.lamp.mindlamp.workers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.WiFi
import digital.lamp.mindlamp.database.AppDatabase
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.notification.LampNotificationManager
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WifiWorkManager(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private lateinit var oGson: Gson
    private lateinit var oAnalyticsDao: AnalyticsDao
    override suspend fun doWork(): Result {
        val notification =
            LampNotificationManager.showNotification(
                context,
                context.getString(digital.lamp.mindlamp.R.string.active_data_collection)
            )
        setForeground(ForegroundInfo(1010, notification))
        oGson = Gson()
        oAnalyticsDao = AppDatabase.getInstance(context).analyticsDao()
        val frequency = inputData.getDouble("frequency", 0.0)
        try {
            Lamp.startWiFi(context)//start sensor
            frequency?.let {
                val interval = (1 / frequency)
                WiFi.setInterval(interval.toLong())
            }
            //Sensor Observer
            WiFi.sensorObserver = object : WiFi.LAMPSensorObserver {
                override fun onWiFiScanEnded() {
                }

                override fun onBluetoothDetected(data: ContentValues?) {
                    if (data != null) {
                        val data = DimensionData(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            data.getAsString("bluetoothAddress"),
                            data.getAsString("bluetoothName"),
                            data.getAsInteger("bluetoothRSSI"),
                            null, null, null, null, null,
                            "Bluetooth", null, null, null
                        )
                        val sensorEventData =
                            SensorEvent(
                                data,
                                Sensors.NEARBY_DEVICES.sensor_name,
                                System.currentTimeMillis().toDouble()
                            )

                        Log.e("Bluetooth :"," ${data.address}")

                        getWifiData(sensorEventData)

                    }
                }

                override fun onWiFiAPDetected(data: ContentValues?) {
                    if (data != null) {
                        val data = DimensionData(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            data.getAsString("bssid"),
                            data.getAsString("ssid"),
                            data.getAsInteger("rssi"),
                            null, null, null, null, null,
                            "wifi", null, null, null
                        )
                        val sensorEventData =
                            SensorEvent(
                                data,
                                Sensors.NEARBY_DEVICES.sensor_name,
                                System.currentTimeMillis().toDouble()
                            )

                        LampLog.e("Wifi : ${data.address}")
                        getWifiData(sensorEventData)

                    }
                }

                override fun onWiFiScanStarted() {
                    LampLog.e("Scan Started")

                }

                override fun onWiFiDisabled() {
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Result.success()
    }
    private fun getWifiData(sensorEventData: SensorEvent) {
        val oAnalytics = Analytics()
        oAnalytics.analyticsData = oGson.toJson(sensorEventData)
        CoroutineScope(Dispatchers.IO).launch {
            oAnalyticsDao.insertAnalytics(oAnalytics)
        }
    }


}