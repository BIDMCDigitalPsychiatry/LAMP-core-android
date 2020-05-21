package digital.lamp.mindlamp.aware

import android.content.Context
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Gyroscope
import com.aware.providers.Gyroscope_Provider
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.network.model.UserAgent
import digital.lamp.mindlamp.utils.Utils

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class GyroscopeData constructor(awareListener: AwareListener, context: Context) {
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
                val x = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_0)
                val y = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_1)
                val z = it.getAsDouble(Gyroscope_Provider.Gyroscope_Data.VALUES_2)
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
                            null,null,null
                        )
                    val sensorEventData =
                        SensorEventData(
                            dimensionData,
                            "lamp.gyroscope",System.currentTimeMillis()
                        )
                    Aware.stopGyroscope(context)
                    awareListener.getGyroscopeData(sensorEventData)
                }else{
                    val logEventRequest = LogEventRequest(context.getString(R.string.log_gyroscope_null), UserAgent(), AppState.session.userId)
                    LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.warning), logEventRequest)
                }
            }
        }catch (ex : Exception){
            val logEventRequest = LogEventRequest(context.getString(R.string.log_gyroscope_error), UserAgent(), AppState.session.userId)
            LogUtils.invokeLogData(Utils.getApplicationName(context), context.getString(R.string.error), logEventRequest)
        }
    }
}