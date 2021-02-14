package digital.lamp.mindlamp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.ArrayList

@Dao
interface SensorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSensors(oSensorsList: ArrayList<SensorSpecs>)

    @Query("SELECT * from sensor_table")
    suspend fun getSensorsList(): List<SensorSpecs>

    @Query("DELETE from sensor_table")
    suspend fun deleteSensorList()
}