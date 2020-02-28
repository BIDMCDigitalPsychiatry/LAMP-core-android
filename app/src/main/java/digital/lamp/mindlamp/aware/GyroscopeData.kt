package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Gyroscope
import com.aware.providers.Gyroscope_Provider
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.SensorEventRequest

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GyroscopeData constructor(awareListener: AwareListener, context: Context) {
    init {
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
            val x = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_0)
            val y = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_1)
            val z = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_2)
            //val value=it.
            if(it != null){
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
                        null
                    )
                val sensorEventRequest =
                    SensorEventRequest(
                        dimensionData,
                        "lamp.gyroscope",
                        System.currentTimeMillis()
                    )
                Aware.stopGyroscope(context)
                awareListener.getGyroscopeData(sensorEventRequest)
            }
        }
    }
}