package digital.lamp.mindlamp.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_table")
data class SensorSpecs(
    @PrimaryKey(autoGenerate = true)
    var identity:Int?,
    var id: String?,
    var spec: String?,
    var name: String?,
    var frequency : Double?,
    var cellularUpload : Boolean?
){
    @Ignore
    constructor() : this(null,null,null,null,null,null)
}