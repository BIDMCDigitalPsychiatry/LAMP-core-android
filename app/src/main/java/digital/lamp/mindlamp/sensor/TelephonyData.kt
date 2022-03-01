package digital.lamp.mindlamp.sensor

import android.content.Context
import android.util.Log
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.Screen
import digital.lamp.lamp_kotlin.sensor_core.TelephonySensor
import digital.lamp.mindlamp.sensor.constants.SensorConstants
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Sensors
import kotlin.math.floor


/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class TelephonyData constructor(sensorListener: SensorListener, context: Context) {
    init {
        try {
            Lamp.startTelephony(context)//Start Screen Sensor
            //Sensor Observer
            TelephonySensor.setSensorObserver(object :TelephonySensor.TelephonyListener{
                override fun onIncomingCallEnded(callDuration: Int?) {
                    callDuration?.let {
                        val telephonyData = TelephonyData(
                            callDuration,
                            null,
                            SensorConstants.CallState.INCOMING.value
                        )
                        val sensorEventData =
                            SensorEvent(
                                telephonyData,
                                Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                            )
                        sensorListener.getTelephonyData(sensorEventData)
                    }
                }

                override fun onOutgoingCallEnded(callDuration: Int?) {
                    callDuration?.let {

                        val telephonyData = TelephonyData(callDuration, null, SensorConstants.CallState.OUTGOING.value)
                        val sensorEventData =
                            SensorEvent(
                                telephonyData,
                                Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                            )
                        sensorListener.getTelephonyData(sensorEventData)
                    }
                }

                override fun onMissedCall() {
                    val telephonyData = TelephonyData(0, null, SensorConstants.CallState.MISSED.value)
                    val sensorEventData =
                        SensorEvent(
                            telephonyData,
                            Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                        )
                    sensorListener.getTelephonyData(sensorEventData)
                }

            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}