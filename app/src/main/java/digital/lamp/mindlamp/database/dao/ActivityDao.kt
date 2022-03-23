package digital.lamp.mindlamp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import digital.lamp.mindlamp.database.entity.ActivitySchedule
import java.util.ArrayList

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActivity(oActivityScheduleList: ArrayList<ActivitySchedule>)

    @Query("SELECT * from activity_table")
    suspend fun getActivityList(): List<ActivitySchedule>

    @Query("DELETE from activity_table")
    suspend fun deleteActivityList()
}