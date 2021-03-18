package digital.lamp.mindlamp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import digital.lamp.mindlamp.database.dao.ActivityDao
import digital.lamp.mindlamp.database.dao.AnalyticsDao
import digital.lamp.mindlamp.database.dao.SensorDao
import digital.lamp.mindlamp.database.entity.ActivitySchedule
import digital.lamp.mindlamp.database.entity.Analytics
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.database.helper.ScheduleConverter
import digital.lamp.mindlamp.utils.SingletonHolder

@Database(entities = [Analytics::class, SensorSpecs::class, ActivitySchedule::class], version = 3, exportSchema = false)
@TypeConverters(ScheduleConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun sensorDao(): SensorDao
    abstract fun activityDao(): ActivityDao
    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "lamp_database")
            .fallbackToDestructiveMigration()
            .build()
    })
}