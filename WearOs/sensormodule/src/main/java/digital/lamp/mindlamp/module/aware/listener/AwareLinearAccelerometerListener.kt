package lamp.mindlamp.sensormodule.aware

import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareLinearAccelerometerListener {

    //Callback to Acclerometer Data
    fun getLinearAccelerometerData(status: Int, sensorEventData: SensorEventData)

}