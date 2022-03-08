package digital.lamp.mindlamp.database.helper

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import digital.lamp.lamp_kotlin.lamp_core.models.DurationIntervalLegacy
import java.lang.reflect.Type

class ScheduleConverter {
    @TypeConverter
    fun fromString(value: String): ArrayList<DurationIntervalLegacy> {
        val listType = Gson().fromJson(value, Array<DurationIntervalLegacy>::class.java)
        val arrayList = arrayListOf<DurationIntervalLegacy>()
        listType.forEach {
            arrayList.add(it)
        }
        return arrayList
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<DurationIntervalLegacy>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}