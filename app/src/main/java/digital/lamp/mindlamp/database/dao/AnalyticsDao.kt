package digital.lamp.mindlamp.database.dao

import androidx.room.*
import digital.lamp.mindlamp.database.entity.Analytics
import java.util.ArrayList

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(oAnalytics: Analytics)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnalytics(oAnalyticsList: ArrayList<Analytics>)

    @Query("SELECT * from analytics_table WHERE analytics_date_ms > :timestamp ORDER BY analytics_date_ms DESC")
    suspend fun getAnalyticsList(timestamp: Long): List<Analytics>

    @Query("DELETE from analytics_table WHERE analytics_date_ms <= :timestamp")
    suspend fun deleteAnalyticsList(timestamp: Long)

    @Query("DELETE from analytics_table")
    suspend fun dropAnalyticsList()
}