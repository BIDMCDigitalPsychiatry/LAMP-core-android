package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.Lamp
import digital.lamp.Gyroscope
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GyroscopeData constructor(sensorListener: SensorListener, context: Context) {
    init {
        try {
            Lamp.startGyroscope(context)//Start Gyroscope Sensor
            //Sensor Observer
            Gyroscope.setSensorObserver {
                val x = it.getAsDouble(Gyroscope.Gyroscope_Data.VALUES_0)
                val y = it.getAsDouble(Gyroscope.Gyroscope_Data.VALUES_1)
                val z = it.getAsDouble(Gyroscope.Gyroscope_Data.VALUES_2)
                //val value=it.
                if (it != null) {
                    val dimensionData =
                        DimensionData(
                            x,
                            y,
                            z,
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
                            null,null,null,null
                        )
                    val sensorEventData =
                        SensorEventData(
                            dimensionData,
                            "lamp.gyroscope",System.currentTimeMillis().toDouble()
                        )
                    LampLog.e("Gyroscope : $x : $y : $z")

//                    Aware.stopGyroscope(context)
                    sensorListener.getGyroscopeData(sensorEventData)
                }else{
                    val logEventRequest = LogEventRequest()
                    logEventRequest.message = context.getString(R.string.log_gyroscope_null)
                    LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.warning), logEventRequest)
                }
            }
        }catch (ex : Exception){
            val logEventRequest = LogEventRequest()
            logEventRequest.message = context.getString(R.string.log_gyroscope_error)
            LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
        }
    }
}