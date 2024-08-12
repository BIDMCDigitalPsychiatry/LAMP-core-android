package digital.lamp.mindlamp.model

import java.util.Date

data class ActivityEventData(
    val duration: Int?,
    val static_data: Any,
    val temporal_slices: Any,
    val activity: String?,
    val timestamp: Long?,
    val _parent: String?
){
    val date: Date?
        get() = timestamp?.let { Date(it) }
}
data class DataWrapper(
    val data: List<ActivityEventData>
)