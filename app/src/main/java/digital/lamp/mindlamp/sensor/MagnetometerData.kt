package digital.lamp.mindlamp.sensor

import android.content.Context
import com.mindlamp.Lamp
import com.mindlamp.Lamp_Preferences
import com.mindlamp.Magnetometer
import com.mindlamp.Rotation
import com.mindlamp.providers.Magnetometer_Provider
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.network.model.*
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class MagnetometerData constructor(sensorListener: SensorListener, context: Context){
    init {
        try {
            //Rotation Sensor Settings
            Lamp.setSetting(
                context,
                Lamp_Preferences.FREQUENCY_MAGNETOMETER,
                200000
            ) //20Hz
            Lamp.setSetting(context, Lamp_Preferences.THRESHOLD_MAGNETOMETER, 5f)
            Lamp.startMagnetometer(context)//start Sensor
            //Sensor Observer
            Magnetometer.setSensorObserver {
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
                        null,null,null,null
                    )
                    val sensorEventData =
                        SensorEventData(
                            data,
                            "lamp.accelerometer.motion",System.currentTimeMillis().toDouble()
                        )
                    LampLog.e("Magnetometer : $x : $y : $z")

//                    Aware.startMagnetometer(context)
                    sensorListener.getMagneticData(sensorEventData)
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