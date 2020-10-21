package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Rotation
import com.aware.providers.Magnetometer_Provider
import com.aware.providers.Rotation_Provider
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.network.model.*
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class MagnetometerData constructor(awareListener: AwareListener, context: Context){
    init {
        try {
            //Rotation Sensor Settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_MAGNETOMETER,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_BAROMETER, 5f)
            Aware.startMagnetometer(context)//start Sensor
            //Sensor Observer
            Rotation.setSensorObserver {
                val x = it.getAsDouble(Magnetometer_Provider.Magnetometer_Data.VALUES_0)
                val y = it.getAsDouble(Magnetometer_Provider.Magnetometer_Data.VALUES_1)
                val z = it.getAsDouble(Magnetometer_Provider.Magnetometer_Data.VALUES_2)
                //val value=it.
                if (it != null) {
                    val magnetData =
                        MagnetData(x, y, z)
                    val data = DimensionData(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        magnetData,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,null,null
                    )
                    val sensorEventData =
                        SensorEventData(
                            data,
                            "lamp.accelerometer.motion",System.currentTimeMillis().toDouble()
                        )
                    LampLog.e("Magnetometer : $x : $y : $z")

//                    Aware.startMagnetometer(context)
                    awareListener.getMagneticData(sensorEventData)
                }else{
                    val logEventRequest = LogEventRequest()
                    logEventRequest.message = context.getString(R.string.log_magnetometer_null)
                    LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.warning), logEventRequest)
                }
            }
        }catch (ex : Exception){
            val logEventRequest = LogEventRequest()
            logEventRequest.message = context.getString(R.string.log_magnetometer_error)
            LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
        }
    }
}