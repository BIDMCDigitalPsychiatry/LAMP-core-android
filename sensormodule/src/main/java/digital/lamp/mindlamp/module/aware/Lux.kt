package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Light
import com.aware.providers.Accelerometer_Provider
import com.aware.providers.Barometer_Provider
import com.aware.providers.Light_Provider
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Lux constructor(awareListener: AwareLuxListener, context: Context, sensorname:String) {
    init {
        try {
            //Lux settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_LIGHT,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_LIGHT, 0.02f)
            Aware.startLight(context)//start sensor
            //Sensor Observer
            Light.setSensorObserver {
                val x = it.getAsDouble(Light_Provider.Light_Data.LIGHT_LUX)?: 0.0
                if (it != null) {
                    val dimensionData =
                        DimensionData(
                            x,
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
                            null,
                            null,
                            null, null, null
                        )
                    val sensorEventData =
                        SensorEventData(
                            dimensionData,
                            sensorname, System.currentTimeMillis()
                        )
                    Aware.stopLight(context)
                    awareListener.getLuxData(Constants.SUCCESS, sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getLuxData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getLuxData(Constants.ERROR, sensorEventData)

        }
    }
}