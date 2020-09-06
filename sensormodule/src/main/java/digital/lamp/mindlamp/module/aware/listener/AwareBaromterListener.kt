package lamp.mindlamp.sensormodule.aware

import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareBaromterListener {

    //Callback to Acclerometer Data
    fun getBarometerData(status: Int, sensorEventData: SensorEventData)

}