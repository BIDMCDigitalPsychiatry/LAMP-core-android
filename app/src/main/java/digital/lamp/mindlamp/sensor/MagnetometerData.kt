package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.Lamp
import digital.lamp.Magnetometer
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
            Lamp.startMagnetometer(context)//start Sensor
            //Sensor Observer
            Magnetometer.setSensorObserver {
                val x = it.getAsDouble(Magnetometer.Magnetometer_Data.VALUES_0)
                val y = it.getAsDouble(Magnetometer.Magnetometer_Data.VALUES_1)
                val z = it.getAsDouble(Magnetometer.Magnetometer_Data.VALUES_2)
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