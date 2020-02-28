package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Screen
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.SensorEventRequest


/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class ScreenStateData constructor(awareListener: AwareListener, context: Context){
   init {
        //Screen State Settings
        Aware.setSetting(context, Aware_Preferences.STATUS_SCREEN, true)
        Aware.startScreen(context)//Start Screen Sensor
        //Sensor Observer
        Screen.setSensorObserver(object : Screen.AWARESensorObserver {
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
                    null
                )
                val sensorEventRequest =
                    SensorEventRequest(
                        data,
                        "lamp.screen_state",
                        System.currentTimeMillis()
                    )
                awareListener.getScreenState(sensorEventRequest)
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
                    null
                )
                val sensorEventRequest =
                    SensorEventRequest(
                        data,
                        "lamp.screen_state",
                        System.currentTimeMillis()
                    )
                awareListener.getScreenState(sensorEventRequest)
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
                    null
                )
                val sensorEventRequest =
                    SensorEventRequest(
                        data,
                        "lamp.screen_state",
                        System.currentTimeMillis()
                    )
                awareListener.getScreenState(sensorEventRequest)
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
                    null
                )
                val sensorEventRequest =
                    SensorEventRequest(
                        data,
                        "lamp.screen_state",
                        System.currentTimeMillis()
                    )
                awareListener.getScreenState(sensorEventRequest)
            }
        })
        Aware.stopScreen(context)
    }
}