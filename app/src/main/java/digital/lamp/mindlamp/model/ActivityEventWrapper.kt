package digital.lamp.mindlamp.model

import digital.lamp.lamp_kotlin.lamp_core.models.ActivityEvent

data class ActivityEventWrapper(
    val events: List<ActivityEvent>
)