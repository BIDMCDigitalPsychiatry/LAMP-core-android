package digital.lamp.mindlamp.sensor

import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 * callback from sensor events
 */
interface SensorListener {
    //Callback to Acclerometer Data
    fun getAccelerometerData(sensorEventData: SensorEvent)
    //Callback to Rotation Data
    fun getRotationData(sensorEventData: SensorEvent)
    //Callback to Magnetic Data
    fun getMagneticData(sensorEventData: SensorEvent)
    //Callback to gyroscope Data
    fun getGyroscopeData(sensorEventData: SensorEvent)
    //Callback to Location Data
    fun getLocationData(sensorEventData: SensorEvent)
    //Callback to wifi Data
    fun getWifiData(sensorEventData: SensorEvent)
    //Callback to ScreenState Data
    fun getScreenState(sensorEventData: SensorEvent)
    //Callback to Activity Data
    fun getActivityData(sensorEventData: SensorEvent)
    //Callback to Fitbit Data
    fun getGoogleFitData(sensorEventData: ArrayList<SensorEvent>)
    //Callback to Telephony data
    fun getTelephonyData(sensorEventData: SensorEvent)
    // callback to Google health connect
    fun getGoogleHealthConnect(sensorEventData: ArrayList<SensorEvent>)

}