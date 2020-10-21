package digital.lamp.mindlamp.aware

import android.content.ContentValues
import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.WiFi
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class WifiData constructor(awareListener: AwareListener, context: Context) {
    init{
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
                            null,null,null
                        )
                        val sensorEventData =
                            SensorEventData(
                                data,
                                "lamp.wifi",System.currentTimeMillis().toDouble()
                            )
                        awareListener.getWifiData(sensorEventData)
//                        Handler().postDelayed({
//                            Aware.stopWiFi(context)
//                        }, 3000)
                    }else{
                        val logEventRequest = LogEventRequest()
                        logEventRequest.message = context.getString(R.string.log_wifi_null)
                        LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.warning), logEventRequest)
                    }

                }

                override fun onWiFiScanStarted() {
                    LampLog.e("Scan Started")

                }

                override fun onWiFiDisabled() {
                }
            })
        }catch (ex: Exception){
            val logEventRequest = LogEventRequest()
            logEventRequest.message = context.getString(R.string.log_wifi_error)
            LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
        }
    }
}