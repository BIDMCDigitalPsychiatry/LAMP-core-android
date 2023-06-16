package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.AttitudeData
import digital.lamp.lamp_kotlin.lamp_core.models.DeviceMotionData
import digital.lamp.lamp_kotlin.lamp_core.models.GravityData
import digital.lamp.lamp_kotlin.lamp_core.models.MagnetData
import digital.lamp.lamp_kotlin.lamp_core.models.MotionData
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Gravity
import digital.lamp.lamp_kotlin.sensor_core.Gyroscope
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog


/**
 * Created by ZCO Engineering Dept.
 */
class GravityData constructor(sensorListener: SensorListener, context: Context, frequency:Double?) {
    init {
        try {
            Lamp.startGravity(context)//Start Gyroscope Sensor
            frequency?.let {
                val interval = (1 / frequency!!)*1000
                Gravity.setInterval(interval.toLong())// 1 millisecond
            }
            //Sensor Observer
            Gravity.setSensorObserver {
                val x = it.getAsDouble(Gyroscope.VALUES_0)
                val y = it.getAsDouble(Gyroscope.VALUES_1)
                val z = it.getAsDouble(Gyroscope.VALUES_2)

                val gravityData = GravityData(x,y,z)
                val dimensionData = DeviceMotionData( MotionData(null,null,null),MagnetData(null,null,null),
                    AttitudeData(null,null,null),gravityData,  RotationData(null,null,null))
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