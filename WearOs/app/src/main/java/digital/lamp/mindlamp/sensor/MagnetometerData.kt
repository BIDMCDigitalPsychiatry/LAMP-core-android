package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.AttitudeData
import digital.lamp.lamp_kotlin.lamp_core.models.DeviceMotionData
import digital.lamp.lamp_kotlin.lamp_core.models.GravityData
import digital.lamp.lamp_kotlin.lamp_core.models.MagnetData
import digital.lamp.lamp_kotlin.lamp_core.models.MotionData
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Magnetometer
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class MagnetometerData constructor(sensorListener: SensorListener, context: Context, frequency:Double?){
    init {
        try {
            Lamp.startMagnetometer(context)//start Sensor
            frequency?.let {
                val interval = (1 / frequency!!)*1000
                Magnetometer.setInterval(interval.toLong())// 1 millisecond
            }
            //Sensor Observer
            Magnetometer.setSensorObserver {
                val x = it.getAsDouble(Magnetometer.VALUES_0)
                val y = it.getAsDouble(Magnetometer.VALUES_1)
                val z = it.getAsDouble(Magnetometer.VALUES_2)
                //val value=it.
                val magnetData =
                    MagnetData(x, y, z)
                val data = DeviceMotionData( MotionData(null,null,null),magnetData,
                    AttitudeData(null,null,null),GravityData(null,null,null), RotationData(null,null,null))
                val sensorEventData =
                    SensorEvent(
                        data,
                        Sensors.DEVICE_MOTION.sensor_name,System.currentTimeMillis().toDouble()
                    )
                LampLog.e("Magnetometer : $x : $y : $z")
                sensorListener.getMagneticData(sensorEventData)
            }
        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }
}