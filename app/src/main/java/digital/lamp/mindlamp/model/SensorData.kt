package digital.lamp.mindlamp.model

import androidx.room.ColumnInfo
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent

data class SensorData(
    var id:Int?,
    @ColumnInfo(name = "analytics_data")
    var data: SensorEvent?,
    @ColumnInfo(name = "analytics_date_ms")
    var datetimeMillisecond: Long?
)
