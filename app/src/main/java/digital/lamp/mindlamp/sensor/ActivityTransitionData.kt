package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.lamp_core.models.ActivityData
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.sensor_core.ActivityTransitions
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.utils.Sensors

class ActivityTransitionData constructor(
    sensorListener: SensorListener,
    context: Context,
    sensorSpecList: ArrayList<SensorSpecs>
) {

    companion object {
        private val TAG = ActivityTransitionData::class.java.simpleName
    }

    init {
        try {

            Lamp.startActivityTransition(context)//start sensor

            //Sensor Observer
            ActivityTransitions.setSensorObserver { activityType, transitionType ->
                var activityTransitionDataRequired = false
                if (sensorSpecList.isEmpty()) {
                    activityTransitionDataRequired = false
                } else {
                    sensorSpecList.forEach {
                        if (it.spec == Sensors.ACTIVITY_RECOGNITION.sensor_name) {
                            activityTransitionDataRequired = true
                        }
                    }
                }
                if (activityTransitionDataRequired) {
                    val activityData = ActivityData()
                    when (activityType) {
                        "running" -> {
                            activityData.running = transitionType
                        }
                        "cycling" -> {
                            activityData.cycling = transitionType
                        }
                        "in_car" -> {
                            activityData.automotive = transitionType
                        }
                        "stationary" -> {
                            activityData.stationary = transitionType
                        }
                        "unknown" -> {
                            activityData.unknown = transitionType
                        }
                        "walking" -> {
                            activityData.walking = transitionType
                        }
                        "on_foot" -> {
                            activityData.on_foot = transitionType
                        }
                    }
                    val dimensionData =
                        DimensionData(
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
                            null,
                            null,
                            null,
                            null, null, null,
                            null, null, null, activityData,null,null
                        )
                    val sensorEventData =
                        SensorEvent(
                            dimensionData,
                            Sensors.ACTIVITY_RECOGNITION.sensor_name,
                            System.currentTimeMillis().toDouble()
                        )


                    sensorListener.getActivityData(sensorEventData)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}