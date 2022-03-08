package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Gyroscope
import com.aware.providers.Gyroscope_Provider
import lamp.mindlamp.sensormodule.aware.AwareGyroscopeListener
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class Gyroscope constructor(awareListener: AwareGyroscopeListener, context: Context, sensorname:String) {
    init {
        try {
            //Gyroscope Settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_GYROSCOPE,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_GYROSCOPE, 0.02f)
            Aware.startGyroscope(context)//Start Gyroscope Sensor
            //Sensor Observer
            Gyroscope.setSensorObserver {
                val x = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_0) ?: 0.0
                val y = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_1) ?: 0.0
                val z = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_2) ?: 0.0
                //val value=it.
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
                    Aware.stopGyroscope(context)
                    awareListener.getGyroscopeData(Constants.SUCCESS, sensorEventData)
                } else {
                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getGyroscopeData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )
            awareListener.getGyroscopeData(Constants.ERROR, sensorEventData)

        }
    }
}