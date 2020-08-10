package lamp.mindlamp.sensormodule.aware

import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareWifiListener {

    //Callback to wifi Data
    fun getWifiData(status: Int, sensorEventData: SensorEventData)
}