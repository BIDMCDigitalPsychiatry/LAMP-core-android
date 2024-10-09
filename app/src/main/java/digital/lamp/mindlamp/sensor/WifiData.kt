package digital.lamp.mindlamp.sensor

import android.content.ContentValues
import android.content.Context
import android.util.Log
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.WiFi
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors

/**
 * Represents data from the Wi-Fi data sensor.
 *Created by ZCO Engineering Dept. on 06,February,2020
 * @param sensorListener The listener for Wi-Fi data sensor events.
 * @param context The application context.
 */
class WifiData constructor(sensorListener: SensorListener, context: Context, frequency: Double?) {
    init {
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

                        sensorListener.getWifiData(sensorEventData)

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
                        sensorListener.getWifiData(sensorEventData)

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
    }
}