package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Temperature
import com.aware.providers.Accelerometer_Provider
import com.aware.providers.Barometer_Provider
import com.aware.providers.Light_Provider
import com.aware.providers.Temperature_Provider
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Temperature constructor(awareListener: AwareTemperatureListener, context: Context, sensorname:String) {
    init {
        try {
            //Lux settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_TEMPERATURE,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f)
            Aware.startTemperature(context)//start sensor
            //Sensor Observer
            Temperature.setSensorObserver {
                val x = it.getAsDouble(Temperature_Provider.Temperature_Data.TEMPERATURE_CELSIUS)?: 0.0
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
                    Aware.stopTemperature(context)
                    awareListener.getTemperatureData(Constants.SUCCESS, sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getTemperatureData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getTemperatureData(Constants.ERROR, sensorEventData)

        }
    }
}