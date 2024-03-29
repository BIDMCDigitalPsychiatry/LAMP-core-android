package digital.lamp.mindlamp.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import digital.lamp.lamp_kotlin.lamp_core.models.DurationIntervalLegacy

/**
 * Represents an activity schedule entity for the Room Database.
 *
 * @param identity Unique identifier generated by Room Database (auto-generated).
 * @param id The unique identifier of the activity schedule.
 * @param spec The sensor spec.
 * @param name The name of the activity.
 * @param schedule List of duration intervals for the activity schedule.
 */
@Entity(tableName = "activity_table")
data class ActivitySchedule(
    @PrimaryKey(autoGenerate = true)
    var identity: Int?,
    val id: String?,
    val spec: String?,
    val name: String?,
    val schedule: ArrayList<DurationIntervalLegacy>?
) {
    @Ignore
    constructor() : this(null, null, null, null, null)
}
