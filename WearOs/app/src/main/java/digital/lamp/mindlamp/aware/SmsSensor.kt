package digital.lamp.mindlamp.aware

import android.content.ContentValues
import android.content.Context

import digital.lamp.mindlamp.module.aware.SmsData
import digital.lamp.mindlamp.module.aware.WifiData
import lamp.mindlamp.sensormodule.aware.AwareSMSListener
import lamp.mindlamp.sensormodule.aware.AwareWifiListener
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class SmsSensor constructor(var awareListener: SensorAwareListener, var context: Context) :
    AwareSMSListener {

    init {
        SmsData(this@SmsSensor, context,"")
    }

    override fun getSmsData(status: Int, sensorEventData: SensorEventData) {
        if (status == Constants.SUCCESS) {
            awareListener.getWifiData(sensorEventData)
        } else if (status == Constants.FAILURE) {


        } else {


        }
    }
}