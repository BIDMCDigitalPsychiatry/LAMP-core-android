package digital.lamp.mindlamp.sensor

import android.content.Context
import digital.lamp.lamp_kotlin.sensor_core.ActivityTransitions
import digital.lamp.lamp_kotlin.sensor_core.Lamp
import digital.lamp.lamp_kotlin.lamp_core.models.ActivityData
import digital.lamp.lamp_kotlin.lamp_core.models.DimensionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent

class ActivityTransitionData constructor(sensorListener: SensorListener, context: Context) {

    companion object {
        private val TAG = ActivityTransitionData::class.java.simpleName
    }

    init {
        try {

            Lamp.startActivityTransition(context)//start sensor

            //Sensor Observer
            ActivityTransitions.setSensorObserver { activityType, transitionType ->
                val activityData = ActivityData()
                when(activityType) {
                    "running" -> { activityData.running = transitionType}
                    "cycling" -> { activityData.cycling = transitionType}
                    "in_car" -> { activityData.automotive = transitionType}
                    "stationary" -> { activityData.stationary = transitionType}
                    "unknown" -> { activityData.unknown = transitionType}
                    "walking" -> { activityData.walking = transitionType}
                    "on_foot" -> { activityData.on_foot = transitionType}
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
                        null,null,null,activityData
                    )
                val sensorEventData =
                    SensorEvent(
                        dimensionData,
                        "lamp.activity_recognition", System.currentTimeMillis().toDouble()
                    )
                sensorListener.getActivityData(sensorEventData)
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }
}