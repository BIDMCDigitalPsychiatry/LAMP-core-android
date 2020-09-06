package lamp.mindlamp.sensormodule.aware

import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareGravityListener {

    //Callback to Acclerometer Data
    fun getGravityData(status: Int, sensorEventData: SensorEventData)

}