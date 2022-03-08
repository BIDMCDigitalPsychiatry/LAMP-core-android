package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Barometer
import com.aware.providers.Accelerometer_Provider
import com.aware.providers.Barometer_Provider
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Pressure constructor(awareListener: AwareBaromterListener, context: Context, sensorname:String) {
    init {
        try {
            //Barometer settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_BAROMETER,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_BAROMETER, 0.02f)
            Aware.startBarometer(context)//start sensor
            //Sensor Observer
            Barometer.setSensorObserver {
                val x = it.getAsDouble(Barometer_Provider.Barometer_Data.AMBIENT_PRESSURE)?: 0.0
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
                    Aware.stopBarometer(context)
                    awareListener.getBarometerData(Constants.SUCCESS, sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getBarometerData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getBarometerData(Constants.ERROR, sensorEventData)

        }
    }
}