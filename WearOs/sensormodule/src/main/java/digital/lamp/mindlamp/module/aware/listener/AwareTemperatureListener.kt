package lamp.mindlamp.sensormodule.aware

import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareTemperatureListener {

    //Callback to Acclerometer Data
    fun getTemperatureData(status: Int, sensorEventData: SensorEventData)

}