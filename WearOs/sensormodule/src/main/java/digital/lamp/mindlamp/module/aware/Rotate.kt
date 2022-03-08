package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Rotation
import com.aware.providers.Rotation_Provider
import lamp.mindlamp.sensormodule.aware.AwareAccelerometerListener
import lamp.mindlamp.sensormodule.aware.AwareRotationListener
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.aware.model.RotationData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class Rotate constructor(awareListener: AwareRotationListener, context: Context, sensorname:String) {
    init {
        try {
            //Rotation Sensor Settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_ROTATION,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_ROTATION, 0.02f)
            Aware.startRotation(context)//start Sensor
            //Sensor Observer
            Rotation.setSensorObserver {
                val x = it.getAsDouble(Rotation_Provider.Rotation_Data.VALUES_0)?: 0.0
                val y = it.getAsDouble(Rotation_Provider.Rotation_Data.VALUES_1)?: 0.0
                val z = it.getAsDouble(Rotation_Provider.Rotation_Data.VALUES_2)?: 0.0
                //val value=it.
                if (it != null) {
                    val rotationData =
                        RotationData(x, y, z)
                    val data = DimensionData(
                        null,
                        null,
                        null,
                        rotationData,
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
                            data,
                            sensorname, System.currentTimeMillis()
                        )
                    Aware.stopRotation(context)
                    awareListener.getRotationData(Constants.SUCCESS, sensorEventData)
                } else {
                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )
                    awareListener.getRotationData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {

            ex.printStackTrace()
            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getRotationData(Constants.ERROR, sensorEventData)
        }
    }
}