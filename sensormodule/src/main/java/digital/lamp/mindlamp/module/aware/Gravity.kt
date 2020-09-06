package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Gravity
import com.aware.Gravity.setSensorObserver
import com.aware.providers.*
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Gravity constructor(
    awareListener: AwareGravityListener,
    context: Context,
    sensorname: String
) {
    init {
        try {
            //gravity settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_GRAVITY,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_GRAVITY, 0.02f)
            Aware.startGravity(context)//start sensor
            //Sensor Observer
            Gravity.setSensorObserver {
                val x = it.getAsDouble(Gravity_Provider.Gravity_Data.VALUES_0) ?: 0.0
                val y = it.getAsDouble(Gravity_Provider.Gravity_Data.VALUES_1) ?: 0.0
                val z = it.getAsDouble(Gravity_Provider.Gravity_Data.VALUES_2) ?: 0.0
                if (it != null) {
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
                            null, null, null
                        )
                    val sensorEventData =
                        SensorEventData(
                            dimensionData,
                            sensorname, System.currentTimeMillis()
                        )
                    Aware.stopGravity(context)
                    awareListener.getGravityData(Constants.SUCCESS, sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getGravityData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getGravityData(Constants.ERROR, sensorEventData)

        }
    }
}