package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Magnetometer
import com.aware.Rotation
import com.aware.providers.Magnetometer_Provider
import com.aware.providers.Rotation_Provider
import lamp.mindlamp.sensormodule.R
import lamp.mindlamp.sensormodule.aware.AwareMagnetometerListener
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.aware.model.MagnetData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class Magnetometer constructor(awareListener: AwareMagnetometerListener, context: Context, sensorname:String) {
    init {
        try {
            //Rotation Sensor Settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_MAGNETOMETER,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_MAGNETOMETER, 0.02f)
            Aware.startMagnetometer(context)//start Sensor
            //Sensor Observer
            Magnetometer.setSensorObserver {
                val x = it.getAsDouble(Magnetometer_Provider.Magnetometer_Data.VALUES_0)?: 0.0
                val y = it.getAsDouble(Magnetometer_Provider.Magnetometer_Data.VALUES_1)?: 0.0
                val z = it.getAsDouble(Magnetometer_Provider.Magnetometer_Data.VALUES_2)?: 0.0
                //val value=it.
                if (it != null) {
                    val magnetData =
                        MagnetData(x, y, z)
                    val data = DimensionData(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        magnetData,
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
                            data,
                            sensorname, System.currentTimeMillis()
                        )
                    Aware.stopMagnetometer(context)
                    awareListener.getMagneticData(Constants.SUCCESS, sensorEventData)
                } else {
                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getMagneticData(Constants.FAILURE, sensorEventData)
                }
            }
        } catch (ex: Exception) {
            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getMagneticData(Constants.ERROR, sensorEventData)
        }
    }
}