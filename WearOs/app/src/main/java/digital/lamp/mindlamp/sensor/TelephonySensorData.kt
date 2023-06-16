package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.TelephonyData
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.sensor_core.TelephonySensor
import digital.lamp.mindlamp.sensor.constants.SensorConstants
import digital.lamp.mindlamp.sensor.utils.Sensors
import digital.lamp.mindlamp.utils.DebugLogs


/**
 * Created by ZCO Engineering Dept. on 07,February,2020
 */
class TelephonySensorData constructor(sensorListener: SensorListener, context: Context) {
    init {
        try {
            Lamp.startTelephony(context)//Start Screen Sensor
            //Sensor Observer
            TelephonySensor.setSensorObserver(object :TelephonySensor.TelephonyListener{
                override fun onIncomingCallEnded(callDuration: Int?) {
                    callDuration?.let {
                        DebugLogs.writeToFile("Incoming cAll : $callDuration time ${System.currentTimeMillis().toDouble()}")
                        val telephonyData = TelephonyData(
                            callDuration,
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
                        DebugLogs.writeToFile("Outgoing cAll : $callDuration time ${System.currentTimeMillis().toDouble()}")
                        val telephonyData = TelephonyData(callDuration,  SensorConstants.CallState.OUTGOING.value)
                        val sensorEventData =
                            SensorEvent(
                                telephonyData,
                                Sensors.TELEPHONY.sensor_name, System.currentTimeMillis().toDouble()
                            )
                        sensorListener.getTelephonyData(sensorEventData)
                    }
                }

                override fun onMissedCall() {
                    DebugLogs.writeToFile("missed cAll :  time ${System.currentTimeMillis().toDouble()}")
                    val telephonyData = TelephonyData(0,  SensorConstants.CallState.MISSED.value)
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