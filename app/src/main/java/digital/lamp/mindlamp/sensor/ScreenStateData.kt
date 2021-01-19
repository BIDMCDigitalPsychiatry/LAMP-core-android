package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Screen
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import java.lang.Exception


/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class ScreenStateData constructor(sensorListener: SensorListener, context: Context){
   init {
       try {
           Lamp.startScreen(context)//Start Screen Sensor
           //Sensor Observer
           Screen.sensorObserver = object : Screen.LAMPSensorObserver {
               override fun onScreenLocked() {
                   val data = DimensionData(
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
                       2,
                       null,
                       null,
                       null,
                       null,
                       null,null,null,null
                   )
                   val sensorEventData =
                       SensorEvent(
                           data,
                           "lamp.screen_state",System.currentTimeMillis().toDouble()
                       )

                   LampLog.e("Screen State : Locked")

                   sensorListener.getScreenState(sensorEventData)
               }

               override fun onScreenOff() {
                   val data = DimensionData(
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
                       0,
                       null,
                       null,
                       null,
                       null,
                       null,null,null,null
                   )
                   val sensorEventRequest =
                       SensorEvent(
                           data,
                           "lamp.screen_state",System.currentTimeMillis().toDouble()
                       )

                   LampLog.e("Screen State : Off")

                   sensorListener.getScreenState(sensorEventRequest)
               }

               override fun onScreenOn() {
                   val data = DimensionData(
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
                       1,
                       null,
                       null,
                       null,
                       null,
                       null,null,null,null
                   )
                   val sensorEventRequest =
                       SensorEvent(
                           data,
                           "lamp.screen_state",System.currentTimeMillis().toDouble()
                       )

                   LampLog.e("Screen State : On")

                   sensorListener.getScreenState(sensorEventRequest)
               }

               override fun onScreenUnlocked() {
                   val data = DimensionData(
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
                       3,
                       null,
                       null,
                       null,
                       null,
                       null,null, null,null
                   )
                   val sensorEventRequest =
                       SensorEvent(
                           data,
                           "lamp.screen_state",System.currentTimeMillis().toDouble()
                       )
                   sensorListener.getScreenState(sensorEventRequest)
               }
           }
       }catch (ex : Exception){
          ex.printStackTrace()
       }
   }
}