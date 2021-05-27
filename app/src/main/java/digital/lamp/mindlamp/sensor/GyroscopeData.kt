package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Gyroscope
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.GravityData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer
import digital.lamp.mindlamp.utils.Sensors
import java.util.concurrent.TimeUnit

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GyroscopeData constructor(sensorListener: SensorListener, context: Context, frequency:Double?) {
    init {
        try {
            Lamp.startGyroscope(context)//Start Gyroscope Sensor
            frequency?.let {
                val interval = TimeUnit.SECONDS.toMillis((1 / frequency!!).toLong())
                Gyroscope.setInterval(interval)// 1 millisecond
            }
            //Sensor Observer
            Gyroscope.setSensorObserver {
                val x = it.getAsDouble(Gyroscope.VALUES_0)
                val y = it.getAsDouble(Gyroscope.VALUES_1)
                val z = it.getAsDouble(Gyroscope.VALUES_2)

                val gravityData = GravityData(x,y,z)
                val dimensionData =
                    DimensionData(
                        null,
                        null,
                        null,
                        null,
                        null,
                        gravityData,
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
                        null,null,null,null,null,null,null,null, null
                    )
                val sensorEventData =
                    SensorEvent(
                        dimensionData,
                        Sensors.DEVICE_MOTION.sensor_name,System.currentTimeMillis().toDouble()
                    )
                LampLog.e("Gyroscope : $x : $y : $z")
                sensorListener.getGyroscopeData(sensorEventData)
            }
        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }
}