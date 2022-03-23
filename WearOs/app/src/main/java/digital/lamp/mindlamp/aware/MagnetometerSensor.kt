package digital.lamp.mindlamp.aware

import android.content.Context
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.LogEventRequest
import digital.lamp.mindlamp.model.UserAgent
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import lamp.mindlamp.sensormodule.aware.AwareMagnetometerListener
import lamp.mindlamp.sensormodule.aware.Magnetometer
import lamp.mindlamp.sensormodule.aware.Rotate
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 06,February,2020
 */
class MagnetometerSensor constructor(
    var awareListener: SensorAwareListener,
    var context: Context,
    var dataViewModel: DataViewModel
) :
    AwareMagnetometerListener {

    init {
        Magnetometer(this@MagnetometerSensor, context,"lamp.android.watch.magnetometer")
    }

    override fun getMagneticData(status: Int, sensorEventData: SensorEventData) {

        if (status == Constants.SUCCESS) {
            awareListener.getMagneticData(sensorEventData)
        } else if (status == Constants.FAILURE) {
            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_magnetometer_null),
                UserAgent(),
                AppState.session.userId
            )
            /*dataViewModel.addLogEvent(
                Utils.getApplicationName(context),
                context.getString(R.string.warning),
                logEventRequest
            )
*/

        } else {

            val logEventRequest = LogEventRequest(
                context.getString(R.string.log_magnetometer_error),
                UserAgent(),
                AppState.session.userId
            )
            /*dataViewModel.addLogEvent(
                Utils.getApplicationName(context),
                context.getString(R.string.error),
                logEventRequest
            )*/
        }
    }

}