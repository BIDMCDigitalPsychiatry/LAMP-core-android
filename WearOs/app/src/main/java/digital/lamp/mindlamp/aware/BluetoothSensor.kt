package digital.lamp.mindlamp.aware

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.Bluetooth
import lamp.mindlamp.sensormodule.aware.AwareBlueToothListener
import lamp.mindlamp.sensormodule.aware.BluetoothData
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import lamp.mindlamp.sensormodule.constant.Constants

/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class BluetoothSensor constructor(var awareListener: SensorAwareListener, var context: Context) :
    AwareBlueToothListener {
    init {
        BluetoothData(this@BluetoothSensor, context,"")
    }

    override fun getBluetoothData(status: Int, sensorEventData: SensorEventData) {

        if (status == Constants.SUCCESS) {
            awareListener.getBluetoothData(sensorEventData)
        } else if (status == Constants.FAILURE) {


        } else {


        }

    }

}