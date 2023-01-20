package digital.lamp.mindlamp.database.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
@Entity(tableName = "analytics_table")
data class Analytics(
    @PrimaryKey(autoGenerate = true)
    var id:Int?,
    @ColumnInfo(name = "analytics_data")
    var analyticsData: String?,
    @ColumnInfo(name = "analytics_date_ms")
    var datetimeMillisecond: Long?
){
    @Ignore
    constructor() : this(null,"",System.currentTimeMillis())
}
