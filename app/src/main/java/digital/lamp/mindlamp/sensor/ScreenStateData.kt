package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Screen
import digital.lamp.mindlamp.sensor.constants.SensorConstants
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Sensors


/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class ScreenStateData constructor(sensorListener: SensorListener, context: Context) {
    init {
        try {
            SensorSpec
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
                        null,
                        SensorConstants.ScreenStateRepresentation.SCREEN_LOCKED.value,
                        NetworkUtils.getBatteryPercentage(context),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        SensorConstants.ScreenState.SCREEN_LOCKED.value,
                        null,
                        null,
                        null, null
                    )
                    val sensorEventData =
                        SensorEvent(
                            data,
                            Sensors.SCREEN_STATE.sensor_name, System.currentTimeMillis().toDouble()
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
                        null,
                            SensorConstants.ScreenStateRepresentation.SCREEN_OFF.value,
                            NetworkUtils.getBatteryPercentage(context),
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_OFF.value,null,  null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.SCREEN_STATE.sensor_name, System.currentTimeMillis().toDouble()
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
                        null,
                            SensorConstants.ScreenStateRepresentation.SCREEN_ON.value,
                            NetworkUtils.getBatteryPercentage(context),
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_ON.value,null,  null,null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.SCREEN_STATE.sensor_name, System.currentTimeMillis().toDouble()
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
                        null,
                            SensorConstants.ScreenStateRepresentation.SCREEN_UNLOCKED.value,
                            NetworkUtils.getBatteryPercentage(context),
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_UNLOCKED.value,null,  null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.SCREEN_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    sensorListener.getScreenState(sensorEventRequest)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}