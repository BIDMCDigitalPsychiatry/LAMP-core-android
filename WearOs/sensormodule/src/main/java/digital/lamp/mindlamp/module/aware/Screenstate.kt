package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Screen
import com.aware.providers.Accelerometer_Provider
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants
import java.lang.Exception


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Screenstate constructor(
    var awareListener: AwareScreenStateListener,
    var context: Context,
    var sensorname: String
) {

    init {
        try {
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
                        null, null, null
                    )
                    val sensorEventData =
                        SensorEventData(
                            data,
                            sensorname, System.currentTimeMillis()
                        )
                    awareListener.getScreenState(Constants.SUCCESS, sensorEventData)
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
                        null, null, null
                    )
                    val sensorEventRequest =
                        SensorEventData(
                            data,
                            sensorname, System.currentTimeMillis()
                        )
                    awareListener.getScreenState(Constants.SUCCESS, sensorEventRequest)
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
                        null, null, null
                    )
                    val sensorEventRequest =
                        SensorEventData(
                            data,
                            sensorname, System.currentTimeMillis()
                        )
                    awareListener.getScreenState(Constants.SUCCESS, sensorEventRequest)
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
                        null, null, null
                    )
                    val sensorEventRequest =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )
                    awareListener.getScreenState(Constants.FAILURE, sensorEventRequest)
                }
            })
            Aware.stopScreen(context)
        } catch (ex: Exception) {

            val sensorEventRequest =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )
            awareListener.getScreenState(Constants.ERROR, sensorEventRequest)
        }
    }
}