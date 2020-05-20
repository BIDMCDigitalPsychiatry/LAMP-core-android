package digital.lamp.mindlamp.network.model

data class SensorEventRequest(
    val timestamp: Long,
    val dataList : ArrayList<SensorEventData>
)