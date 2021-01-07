package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.sensor.Lamp
import digital.lamp.sensor.Gyroscope
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.service.models.DimensionData
import digital.lamp.service.models.SensorEvent

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GyroscopeData constructor(sensorListener: SensorListener, context: Context) {
    init {
        try {
            Lamp.startGyroscope(context)//Start Gyroscope Sensor
            //Sensor Observer
            Gyroscope.setSensorObserver {
                val x = it.getAsDouble(Gyroscope.VALUES_0)
                val y = it.getAsDouble(Gyroscope.VALUES_1)
                val z = it.getAsDouble(Gyroscope.VALUES_2)
                //val value=it.
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
                    SensorEvent(
                        dimensionData,
                        "lamp.gyroscope",System.currentTimeMillis().toDouble()
                    )
                LampLog.e("Gyroscope : $x : $y : $z")
                sensorListener.getGyroscopeData(sensorEventData)
            }
        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }
}