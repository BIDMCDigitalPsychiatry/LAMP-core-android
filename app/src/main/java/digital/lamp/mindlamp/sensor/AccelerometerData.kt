package digital.lamp.mindlamp.sensor

import android.content.Context
import android.util.Log
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.lamp_kotlin.lamp_core.models.RotationData
import digital.lamp.lamp_kotlin.sensor_core.Accelerometer
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors
import java.util.concurrent.TimeUnit

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
class AccelerometerData constructor(
    sensorListener: SensorListener,
    context: Context,
    frequency: Double?,
    private var sensorSpec: String
) {
    init {
        try {

            //Accelerometer settings
            Lamp.startAccelerometer(context)//start sensor
            frequency?.let {
                val interval = (1 / frequency!!) * 1000
                Log.e("AccelerometerData", "$interval")
                Accelerometer.setInterval(interval.toLong())
            }
            //Sensor Observer
            Accelerometer.setSensorObserver {
                val x = it.getAsDouble(Accelerometer.VALUES_0)
                val y = it.getAsDouble(Accelerometer.VALUES_1)
                val z = it.getAsDouble(Accelerometer.VALUES_2)

                val motionData = MotionData(x, y, z)

                when (sensorSpec) {
                    Sensors.DEVICE_MOTION.sensor_name -> {
                        val deviceMotionData = DeviceMotionData(
                            motionData,
                            MagnetData(null, null, null),
                            AttitudeData(null, null, null),
                            GravityData(null, null, null),
                            RotationData(null, null, null)
                        )
                        val sensorEventData =
                            SensorEvent(
                                deviceMotionData,
                                Sensors.DEVICE_MOTION.sensor_name,
                                System.currentTimeMillis().toDouble()
                            )

                        sensorListener.getAccelerometerData(sensorEventData)
                    }

                    Sensors.ACCELEROMETER.sensor_name -> {
                        val dimensionData =
                            DimensionData(
                                x,
                                y,
                                z,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null, null, null, null, null, null, null,
                                null,
                                null
                            )
                        val sensorEventDataAccelerometer =
                            SensorEvent(
                                dimensionData,
                                Sensors.ACCELEROMETER.sensor_name,
                                System.currentTimeMillis().toDouble()
                            )

                        LampLog.e("Accelerometer : $x : $y : $z")

                        sensorListener.getAccelerometerData(sensorEventDataAccelerometer)
                    }
                    else -> {
                        val deviceMotionData = DeviceMotionData(
                            motionData,
                            MagnetData(null, null, null),
                            AttitudeData(null, null, null),
                            GravityData(null, null, null),
                            RotationData(null, null, null)
                        )
                        val sensorEventData =
                            SensorEvent(
                                deviceMotionData,
                                Sensors.DEVICE_MOTION.sensor_name,
                                System.currentTimeMillis().toDouble()
                            )

                        sensorListener.getAccelerometerData(sensorEventData)

                        val dimensionData =
                            DimensionData(
                                x,
                                y,
                                z,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null, null, null, null, null, null, null,
                                null,
                                null
                            )
                        val sensorEventDataAccelerometer =
                            SensorEvent(
                                dimensionData,
                                Sensors.ACCELEROMETER.sensor_name,
                                System.currentTimeMillis().toDouble()
                            )

                        LampLog.e("Accelerometer : $x : $y : $z")

                        sensorListener.getAccelerometerData(sensorEventDataAccelerometer)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}