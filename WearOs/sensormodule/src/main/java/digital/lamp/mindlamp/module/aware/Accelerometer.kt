package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.providers.Accelerometer_Provider
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Accelerometer constructor(awareListener: AwareAccelerometerListener, context: Context, sensorname:String) {
    init {
        try {
            //Accelerometer settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_ACCELEROMETER,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f)
            Aware.startAccelerometer(context)//start sensor
            //Sensor Observer
            Accelerometer.setSensorObserver {
                val x = it.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_0) ?: 0.0
                val y = it.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_1) ?: 0.0
                val z = it.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_2) ?: 0.0

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
                    Aware.stopAccelerometer(context)
                    awareListener.getAccelerometerData(Constants.SUCCESS,sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getAccelerometerData(Constants.FAILURE,sensorEventData)

                }

            }




        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getAccelerometerData(Constants.ERROR,sensorEventData)

        }
    }


}