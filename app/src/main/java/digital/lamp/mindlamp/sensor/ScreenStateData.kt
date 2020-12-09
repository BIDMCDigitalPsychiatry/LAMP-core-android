package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.Lamp
import digital.lamp.Screen
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Utils
import java.lang.Exception


/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class ScreenStateData constructor(sensorListener: SensorListener, context: Context){
   init {
       try {
           Lamp.startScreen(context)//Start Screen Sensor
           //Sensor Observer
           Screen.setSensorObserver(object : Screen.LAMPSensorObserver {
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
                       SensorEventData(
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
                       SensorEventData(
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
                       SensorEventData(
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
                       SensorEventData(
                           data,
                           "lamp.screen_state",System.currentTimeMillis().toDouble()
                       )

                   LampLog.e("Screen State : Unlocked")

                   sensorListener.getScreenState(sensorEventRequest)
               }
           })
//           Aware.stopScreen(context)
       }catch (ex : Exception){
           val logEventRequest = LogEventRequest()
           logEventRequest.message = context.getString(R.string.log_screen_state_error)
           LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
       }
   }
}