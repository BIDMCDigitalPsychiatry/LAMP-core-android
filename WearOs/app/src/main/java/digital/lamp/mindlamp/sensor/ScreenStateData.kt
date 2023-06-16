package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.SensorSpec
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Screen
import digital.lamp.mindlamp.sensor.constants.SensorConstants
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils


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
                    val batteryPercentage :Float = (NetworkUtils.getBatteryPercentage(context).toFloat()/100)
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
                            batteryPercentage,
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
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )

                    LampLog.e("Screen State : Locked")

                    sensorListener.getScreenState(sensorEventData)
                }

                override fun onScreenOff() {
                    val batteryPercentage :Float = (NetworkUtils.getBatteryPercentage(context).toFloat()/100)
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
                            batteryPercentage,
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_OFF.value,null,  null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )

                    LampLog.e("Screen State : Off")

                    sensorListener.getScreenState(sensorEventRequest)
                }

                override fun onScreenOn() {
                    val batteryPercentage :Float = (NetworkUtils.getBatteryPercentage(context).toFloat()/100)
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
                            batteryPercentage,
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_ON.value,null,  null,null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )

                    LampLog.e("Screen State : On")

                    sensorListener.getScreenState(sensorEventRequest)
                }

                override fun onScreenUnlocked() {
                    val batteryPercentage :Float = (NetworkUtils.getBatteryPercentage(context).toFloat()/100)
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
                            batteryPercentage,
                        null,
                        null,
                        null,
                        null, null, null, null,
                        SensorConstants.ScreenState.SCREEN_UNLOCKED.value,null,  null, null, null
                    )
                    val sensorEventRequest =
                        SensorEvent(
                            data,
                            Sensors.DEVICE_STATE.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    sensorListener.getScreenState(sensorEventRequest)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}