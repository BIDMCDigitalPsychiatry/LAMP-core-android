package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.sensor.Accelerometer
import digital.lamp.sensor.Lamp
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.service.models.DimensionData
import digital.lamp.service.models.SensorEvent

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
                         null, null, null, null
                     )
                 val sensorEventData =
                     SensorEvent(
                         dimensionData,
                         "lamp.accelerometer",System.currentTimeMillis().toDouble()
                     )

                 LampLog.e("Accelerometer : $x : $y : $z")
                 sensorListener.getAccelerometerData(sensorEventData)
             }
         }catch (ex:Exception){
             ex.printStackTrace()
         }
    }
}