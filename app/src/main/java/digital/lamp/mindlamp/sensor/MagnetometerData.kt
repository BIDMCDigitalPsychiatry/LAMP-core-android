package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Magnetometer
import digital.lamp.lamp_kotlin.sensor_core.Rotation
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors
import java.util.concurrent.TimeUnit

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