package digital.lamp.mindlamp.aware

import digital.lamp.mindlamp.network.model.SensorEventRequest

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareListener {
    //Callback to Acclerometer Data
    fun getAccelerometerData(sensorEventRequest: SensorEventRequest)
    //Callback to Rotation Data
    fun getRotationData(sensorEventRequest: SensorEventRequest)
    //Callback to Magnetic Data
    fun getMagneticData(sensorEventRequest: SensorEventRequest)
    //Callback to gyroscope Data
    fun getGyroscopeData(sensorEventRequest: SensorEventRequest)
    //Callback to Location Data
    fun getLocationData(sensorEventRequest: SensorEventRequest)
    //Callback to wifi Data
    fun getWifiData(sensorEventRequest: SensorEventRequest)
    //Callback to ScreenState Data
    fun getScreenState(sensorEventRequest: SensorEventRequest)
    //Callback to SMS data
    fun getSmsData(sensorEventRequest: SensorEventRequest)
    //Callback to Bluetooth Data
    fun getBluetoothData(sensorEventRequest: SensorEventRequest)
    //Callback to Fitbit Data
    fun getFitbitData(sensorEventRequest: SensorEventRequest)
}