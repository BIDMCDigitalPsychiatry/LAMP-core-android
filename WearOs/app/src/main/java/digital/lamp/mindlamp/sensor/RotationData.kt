package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.AttitudeData
import digital.lamp.lamp_kotlin.lamp_core.models.DeviceMotionData
import digital.lamp.lamp_kotlin.lamp_core.models.GravityData
import digital.lamp.lamp_kotlin.lamp_core.models.MagnetData
import digital.lamp.lamp_kotlin.lamp_core.models.MotionData
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Gyroscope
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Rotation
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class RotationData constructor(
    sensorListener: SensorListener,
    context: Context,
    frequency: Double?
) {
    init {
        try {

            Lamp.startGyroscope(context)//start Sensor
            frequency?.let {
                val interval = (1 / frequency!!) * 1000
                Gyroscope.setInterval(interval.toLong())
            }
            //Sensor Observer
            Gyroscope.setSensorObserver {
                val x = it.getAsDouble(Rotation.VALUES_0)
                val y = it.getAsDouble(Rotation.VALUES_1)
                val z = it.getAsDouble(Rotation.VALUES_2)
                //val value=it.
                val rotationData =
                    RotationData(x, y, z)
                val data = DeviceMotionData(
                    MotionData(null, null, null), MagnetData(null, null, null),
                    AttitudeData(null, null, null), GravityData(null, null, null), rotationData
                )
                val sensorEventData =
                    SensorEvent(
                        data,
                        Sensors.DEVICE_MOTION.sensor_name, System.currentTimeMillis().toDouble()
                    )
                LampLog.e("Rotation : $x : $y : $z")
                sensorListener.getRotationData(sensorEventData)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}