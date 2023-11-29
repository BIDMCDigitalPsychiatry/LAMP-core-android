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

/**
 * Annotation to define a Room Database.
 *
 * @param entities An array of entity classes that represent tables in the database.
 * @param version The version of the database. Incrementing the version number triggers a database migration.
 * @param exportSchema If set to true, the schema of the database is exported to a folder. Typically used for debugging.
 */
@Database(
    entities = [Analytics::class, SensorSpecs::class, ActivitySchedule::class],
    version = 4,
    exportSchema = false
)
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