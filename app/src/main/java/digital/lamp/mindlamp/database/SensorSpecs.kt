package digital.lamp.mindlamp.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_table")
data class SensorSpecs(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var identity:Int?,
    var id: String?,
    var spec: String?,
    var name: String?
){
    @Ignore
    constructor() : this(null,null,null,null)
}