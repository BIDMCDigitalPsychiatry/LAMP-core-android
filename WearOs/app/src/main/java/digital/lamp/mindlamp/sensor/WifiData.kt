package digital.lamp.mindlamp.sensor

import android.content.ContentValues
import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.WiFi
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog


/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class WifiData constructor(sensorListener: SensorListener, context: Context) {
    init{
        try {
            Lamp.startWiFi(context)//start sensor
            //Sensor Observer
            WiFi.sensorObserver = object : WiFi.LAMPSensorObserver {
                override fun onWiFiScanEnded() {
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
                            null,null,null,null,null,
                            null,null, null,null
                        )
                        val sensorEventData =
                            SensorEvent(
                                data,
                                Sensors.NEARBY_DEVICES.sensor_name,System.currentTimeMillis().toDouble()
                            )

                        LampLog.e("Wifi : ${data.bssid}")

                        sensorListener.getWifiData(sensorEventData)

                    }
                }

                override fun onWiFiScanStarted() {
                    LampLog.e("Scan Started")

                }

                override fun onWiFiDisabled() {
                }
            }
        }catch (ex: Exception){
           ex.printStackTrace()
        }
    }
}