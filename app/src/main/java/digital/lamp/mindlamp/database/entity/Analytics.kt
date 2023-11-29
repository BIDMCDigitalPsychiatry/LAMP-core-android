package digital.lamp.mindlamp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Represents an analytics entity for the Room Database.
 *
 * @param id Unique identifier generated by Room Database (auto-generated).
 * @param analyticsData The JSON or serialized data containing analytics information.
 * @param datetimeMillisecond The timestamp in milliseconds indicating when the analytics data was recorded.
 */
@Entity(tableName = "analytics_table")
data class Analytics(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = "analytics_data")
    var analyticsData: String?,
    @ColumnInfo(name = "analytics_date_ms")
    var datetimeMillisecond: Long?
) {
    @Ignore
    constructor() : this(null, "", System.currentTimeMillis())
}
