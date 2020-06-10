package digital.lamp.mindlamp.aware

import digital.lamp.mindlamp.network.model.SensorEventData

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface AwareListener {

    //Callback to Watch Data
    fun getWatchData(sensorEventData: ArrayList<SensorEventData>)
    //Callback to Acclerometer Data
    fun getAccelerometerData(sensorEventData: SensorEventData)
    //Callback to Rotation Data
    fun getRotationData(sensorEventData: SensorEventData)
    //Callback to Magnetic Data
    fun getMagneticData(sensorEventData: SensorEventData)
    //Callback to gyroscope Data
    fun getGyroscopeData(sensorEventData: SensorEventData)
    //Callback to Location Data
    fun getLocationData(sensorEventData: SensorEventData)
    //Callback to wifi Data
    fun getWifiData(sensorEventData: SensorEventData)
    //Callback to ScreenState Data
    fun getScreenState(sensorEventData: SensorEventData)
    //Callback to SMS data
    fun getSmsData(sensorEventData: SensorEventData)
    //Callback to Bluetooth Data
    fun getBluetoothData(sensorEventData: SensorEventData)
    //Callback to Fitbit Data
    fun getGoogleFitData(sensorEventData: ArrayList<SensorEventData>)
}