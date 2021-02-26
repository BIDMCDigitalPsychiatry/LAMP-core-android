package digital.lamp.mindlamp.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import digital.lamp.lamp_kotlin.lamp_core.models.DurationIntervalLegacy

@Entity(tableName = "activity_table")
data class ActivitySchedule(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var identity:Int?,
    val id: Int?,
    val spec: String?,
    val name: String?,
    val schedule: ArrayList<DurationIntervalLegacy>?
)
{
    @Ignore
    constructor() : this(null,null, null,null,null)
}
