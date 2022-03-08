package digital.lamp.mindlamp.module.aware

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.util.Log
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.WiFi
import lamp.mindlamp.sensormodule.aware.AwareWifiListener
import lamp.mindlamp.sensormodule.aware.Magnetometer
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class WifiData constructor(awareListener: AwareWifiListener, context: Context, sensorname:String) {

    init {
        try {
            //Wifi sensor settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_WIFI,
                5
            )
            Aware.setSetting(context, Aware_Preferences.STATUS_WIFI, true)
            Aware.startWiFi(context)//start sensor
            //Sensor Observer
            WiFi.setSensorObserver(object : WiFi.AWARESensorObserver {
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
                            data.getAsString("bssid"),
                            data.getAsString("ssid"),
                            null,
                            null,
                            null, null, null
                        )
                        val sensorEventData =
                            SensorEventData(
                                data,
                                sensorname, System.currentTimeMillis()
                            )
                        awareListener.getWifiData(Constants.SUCCESS, sensorEventData)
                        Handler().postDelayed({
                            Aware.stopWiFi(context)
                        }, 3000)
                    } else {
                        val sensorEventData =
                            SensorEventData(
                                null,
                                sensorname, System.currentTimeMillis()
                            )
                        awareListener.getWifiData(Constants.FAILURE, sensorEventData)
                    }

                }

                override fun onWiFiScanStarted() {
                    Log.e("WiFi", "Scan Started")

                }

                override fun onWiFiDisabled() {
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getWifiData(Constants.ERROR, sensorEventData)
        }
    }
}