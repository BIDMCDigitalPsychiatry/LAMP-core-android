package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.lamp_core.models.AttitudeData
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.sensor_core.Gravity
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Gyroscope
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors
import java.util.concurrent.TimeUnit

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GravityData constructor(sensorListener: SensorListener, context: Context, frequency:Double?) {
    init {
        try {
            Lamp.startGravity(context)//Start Gyroscope Sensor
            frequency?.let {
                val interval = TimeUnit.SECONDS.toMillis((1 / frequency!!).toLong())
                Gravity.setInterval(interval)// 1 millisecond
            }
            //Sensor Observer
            Gravity.setSensorObserver {
                val x = it.getAsDouble(Gyroscope.VALUES_0)
                val y = it.getAsDouble(Gyroscope.VALUES_1)
                val z = it.getAsDouble(Gyroscope.VALUES_2)

                val gravityData = GravityData(x,y,z)
                val dimensionData = DeviceMotionData( MotionData(null,null,null),MagnetData(null,null,null),
                    AttitudeData(null,null,null),gravityData,  RotationData(null,null,null))
                  /* DimensionData(
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
                    )*/
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