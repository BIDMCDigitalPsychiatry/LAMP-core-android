package lamp.mindlamp.sensormodule.aware

import android.content.Context
import com.aware.Accelerometer
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Proximity
import com.aware.providers.*
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.aware.model.DimensionData
import lamp.mindlamp.sensormodule.constant.Constants


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class Proximity constructor(awareListener: AwareProximityListener, context: Context, sensorname:String) {
    init {
        try {
            //Lux settings
            Aware.setSetting(
                context,
                Aware_Preferences.FREQUENCY_PROXIMITY,
                200000
            ) //20Hz
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_PROXIMITY, 0.02f)
            Aware.startLight(context)//start sensor
            //Sensor Observer
            Proximity.setSensorObserver {
                val x = it.getAsDouble(Proximity_Provider.Proximity_Data.PROXIMITY)?: 0.0
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
                    Aware.stopProximity(context)
                    awareListener.getProximityData(Constants.SUCCESS, sensorEventData)
                } else {

                    val sensorEventData =
                        SensorEventData(
                            null,
                            sensorname, System.currentTimeMillis()
                        )

                    awareListener.getProximityData(Constants.FAILURE, sensorEventData)

                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

            val sensorEventData =
                SensorEventData(
                    null,
                    sensorname, System.currentTimeMillis()
                )

            awareListener.getProximityData(Constants.ERROR, sensorEventData)

        }
    }
}