package digital.lamp.mindlamp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import digital.lamp.mindlamp.utils.SingletonHolder

@Database(entities = [Analytics::class, SensorSpecs::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun sensorDao(): SensorDao
    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "lamp_database")
            .fallbackToDestructiveMigration()
            .build()
    })
}