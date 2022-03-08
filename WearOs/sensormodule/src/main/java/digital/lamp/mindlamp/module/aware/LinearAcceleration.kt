package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.LinearAccelerometer
import com.aware.providers.*
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class LinearAcceleration constructor(awareListener: AwareLinearAccelerometerListener, context: Context, sensorname:String) {
    init {
        try {
            //Linear settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_LINEAR_ACCELEROMETER,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_LINEAR_ACCELEROMETER, 0.02f)
            Aware.startLinearAccelerometer(context)//start sensor
            //Sensor Observer
            LinearAccelerometer.setSensorObserver {
                val x =
                 it.getAsDouble(Linear_Accelerometer_Provider.Linear_Accelerometer_Data.VALUES_0)?: 0.0
                val y =
                 it.getAsDouble(Linear_Accelerometer_Provider.Linear_Accelerometer_Data.VALUES_1)?: 0.0
                val z =
                    it.getAsDouble(Linear_Accelerometer_Provider.Linear_Accelerometer_Data.VALUES_2)?: 0.0
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
                    Aware.stopLinearAccelerometer(context)
                    awareListener.getLinearAccelerometerData(Constants.SUCCESS, sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getLinearAccelerometerData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getLinearAccelerometerData(Constants.ERROR, sensorEventData)

        }
    }
}