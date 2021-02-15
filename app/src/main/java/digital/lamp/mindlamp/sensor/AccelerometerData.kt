package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.MotionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.mindlamp.utils.Sensors

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class AccelerometerData constructor(sensorListener: SensorListener, context:Context) {
     init {
         try {

             //Accelerometer settings
             Lamp.startAccelerometer(context)//start sensor
             //Sensor Observer
             Accelerometer.setSensorObserver {
                 val x = it.getAsDouble(Accelerometer.VALUES_0)
                 val y = it.getAsDouble(Accelerometer.VALUES_1)
                 val z = it.getAsDouble(Accelerometer.VALUES_2)

                 val motionData = MotionData(x,y,z)
                 val dimensionData =
                     DimensionData(
                         null,
                         null,
                         null,
                         null,
                         motionData,
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
                         null, null, null, null,null,null,null
                     )
                 val sensorEventData =
                     SensorEvent(
                         dimensionData,
                         Sensors.DEVICE_MOTION.sensor_name,System.currentTimeMillis().toDouble()
                     )

                 LampLog.e("Accelerometer : $x : $y : $z")
                 sensorListener.getAccelerometerData(sensorEventData)
             }
         }catch (ex:Exception){
             ex.printStackTrace()
         }
    }
}