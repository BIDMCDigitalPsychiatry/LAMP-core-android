package digital.lamp.mindlamp.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
@Entity(tableName = "analytics_table")
data class Analytics(
    @NonNull
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
