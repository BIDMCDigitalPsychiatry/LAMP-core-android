package digital.lamp.mindlamp.sensor

import digital.lamp.mindlamp.network.model.SensorEventData

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
interface SensorListener {
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
    //Callback to Activity Data
    fun getActivityData(sensorEventData: SensorEventData)
    //Callback to Fitbit Data
    fun getGoogleFitData(sensorEventData: ArrayList<SensorEventData>)
}