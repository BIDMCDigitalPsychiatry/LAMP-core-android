package digital.lamp.mindlamp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import digital.lamp.mindlamp.database.entity.Analytics

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(oAnalytics: Analytics):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnalytics(oAnalyticsList: ArrayList<Analytics>)

    @Query("SELECT * from analytics_table WHERE analytics_date_ms > :timestamp ORDER BY analytics_date_ms DESC")
    suspend fun getAnalyticsList(timestamp: Long): List<Analytics>

    @Query("SELECT * from analytics_table WHERE analytics_date_ms > :timestamp ORDER BY analytics_date_ms ASC LIMIT 1")
    suspend fun getFirstAnalyticsRecord(timestamp: Long): Analytics?

    @Query("SELECT * from analytics_table WHERE analytics_date_ms BETWEEN :timestamp and :endTime ORDER BY analytics_date_ms DESC LIMIT 1000")
    suspend fun getAnalyticsList(timestamp: Long, endTime:Long): List<Analytics>

    @Query("SELECT * from analytics_table WHERE analytics_date_ms BETWEEN :timestamp and :endTime ORDER BY analytics_date_ms DESC")
    suspend fun getAnalyticsListForWorker(timestamp: Long, endTime:Long): List<Analytics>

    @Query("SELECT COUNT(*) FROM analytics_table WHERE analytics_date_ms > :timestamp ORDER BY analytics_date_ms DESC")
    suspend fun getNumberOfRecordsToSync(timestamp: Long): Int

    @Query("DELETE from analytics_table WHERE analytics_date_ms <= :timestamp")
    suspend fun deleteAnalyticsList(timestamp: Long)

    @Query("DELETE from analytics_table")
    suspend fun dropAnalyticsList()

    @Query("SELECT * from analytics_table WHERE analytics_date_ms > :timestamp ORDER BY analytics_date_ms DESC LIMIT :limit OFFSET :offset")
    suspend fun getAnalyticsListPaged(timestamp: Long, limit: Int, offset: Int): List<Analytics>
}