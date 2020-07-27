package digital.lamp.mindlamp.standalone.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataMap


/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
@Suppress("DEPRECATION")
class WatchStateData constructor(
    awareListener: View.OnClickListener,
    context: Context,
    googleApiClient: GoogleApiClient
) {
    private var awllistener = awareListener
    private var requestedsensors: DataMap = DataMap()
    private var sensorEventDataList: ArrayList<SensorEventData> = arrayListOf()

    init {

        var arraysensors = arrayOf(
            Sensor.TYPE_ACCELEROMETER.toString(),
            Sensor.TYPE_GYROSCOPE.toString(),
            Sensor.TYPE_GRAVITY.toString(),
            Sensor.TYPE_LINEAR_ACCELERATION.toString(),
            Sensor.TYPE_ROTATION_VECTOR.toString(),
            Sensor.TYPE_RELATIVE_HUMIDITY.toString(),
            Sensor.TYPE_AMBIENT_TEMPERATURE.toString(),
            Sensor.TYPE_STEP_DETECTOR.toString(),
            Sensor.TYPE_STEP_COUNTER.toString(),
            Sensor.TYPE_HEART_RATE.toString(),
            Sensor.TYPE_MAGNETIC_FIELD.toString(),
            Sensor.TYPE_PRESSURE.toString(),
            Sensor.TYPE_LIGHT.toString(),
            Sensor.TYPE_PROXIMITY.toString(),
            Sensor.TYPE_POSE_6DOF.toString(),
            Sensor.TYPE_STATIONARY_DETECT.toString(),
            Sensor.TYPE_MOTION_DETECT.toString(),
            Sensor.TYPE_HEART_BEAT.toString(),
            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()
        )
        requestedsensors.putStringArray("reqeustedsensors", arraysensors)

        Handler().post({

           /* SendToDataLayerThread(
                "/getSensorVals",
                "",
                requestedsensors,
                googleApiClient
            ).run()*/
        })


    }

    fun populateValues(vals: CustomMap) {

        if (((vals.dataMap?.get(Sensor.TYPE_ACCELEROMETER.toString()) as Array<String>).size) > 0 &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_ACCELEROMETER.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_ACCELEROMETER.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.accelerometer", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)

        }

        if ((vals.dataMap?.getStringArray(Sensor.TYPE_GYROSCOPE.toString())) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_GYROSCOPE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_GYROSCOPE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.gyroscope", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)

        }
        if (vals.dataMap?.getStringArray(Sensor.TYPE_GRAVITY.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_GRAVITY.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_GRAVITY.toString()) as Array<String>
            val gravityData =
                GravityData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    null,
                    null,
                    gravityData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.gravity", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_LINEAR_ACCELERATION.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_LINEAR_ACCELERATION.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_LINEAR_ACCELERATION.toString()) as Array<String>

            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.linear", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_ROTATION_VECTOR.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_ROTATION_VECTOR.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_ROTATION_VECTOR.toString()) as Array<String>
            val rotationData =
                RotationData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    rotationData,
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.rotationvector", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_RELATIVE_HUMIDITY.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_RELATIVE_HUMIDITY.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_RELATIVE_HUMIDITY.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.relativehumidity", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_AMBIENT_TEMPERATURE.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_AMBIENT_TEMPERATURE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_AMBIENT_TEMPERATURE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.ambiettemperature", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_DETECTOR.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_DETECTOR.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_STEP_DETECTOR.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.stepdetector", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_COUNTER.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_STEP_COUNTER.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_STEP_COUNTER.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.stepcounter", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_RATE.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_RATE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_HEART_RATE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.heartrate", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_MAGNETIC_FIELD.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_MAGNETIC_FIELD.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_MAGNETIC_FIELD.toString()) as Array<String>
            val magnetometerData =
                MagnetData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    magnetometerData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.magneticfield", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_PRESSURE.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_PRESSURE.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_PRESSURE.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.pressure", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_LIGHT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_LIGHT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_LIGHT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.light", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_PROXIMITY.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_PROXIMITY.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_PROXIMITY.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.proximity", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_POSE_6DOF.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_POSE_6DOF.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_POSE_6DOF.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.pose", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_STATIONARY_DETECT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_STATIONARY_DETECT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_STATIONARY_DETECT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.stationary", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_MOTION_DETECT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_MOTION_DETECT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_MOTION_DETECT.toString()) as Array<String>
            val motionData =
                MotionData(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())

            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    null,
                    null,
                    null,
                    null,
                    motionData,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, null,
                    null,
                    null
                ), "lamp.watch.motion", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_BEAT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_HEART_BEAT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_HEART_BEAT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.heartbeat", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }

        if (vals.dataMap?.getStringArray(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()) != null &&
            (vals.dataMap?.getStringArray(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()))!!.size > 0
        ) {
            var values: Array<String> =
                vals.dataMap?.get(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT.toString()) as Array<String>
            val sensorEvenData: SensorEventData = SensorEventData(
                DimensionData(
                    values[0].toDouble(),
                    values[1].toDouble(),
                    values[2].toDouble(),
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
                    null, null,
                    null,
                    null
                ), "lamp.watch.latency", System.currentTimeMillis()
            )

            sensorEventDataList.add(sensorEvenData)
        }


    }


}