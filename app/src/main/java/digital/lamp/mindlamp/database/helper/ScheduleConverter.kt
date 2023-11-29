package digital.lamp.mindlamp.database.helper

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import digital.lamp.lamp_kotlin.lamp_core.models.DurationIntervalLegacy
import java.lang.reflect.Type

/**
 * Room TypeConverter for converting between String and ArrayList<DurationIntervalLegacy>.
 */
class ScheduleConverter {
    /**
     * Converts a String representation to an ArrayList<DurationIntervalLegacy>.
     *
     * @param value The String representation to convert.
     * @return An ArrayList<DurationIntervalLegacy> object.
     */
    @TypeConverter
    fun fromString(value: String): ArrayList<DurationIntervalLegacy> {
        val listType = Gson().fromJson(value, Array<DurationIntervalLegacy>::class.java)
        val arrayList = arrayListOf<DurationIntervalLegacy>()
        listType.forEach {
            arrayList.add(it)
        }
        return arrayList
    }
    /**
     * Converts an ArrayList<DurationIntervalLegacy> to its String representation.
     *
     * @param list The ArrayList<DurationIntervalLegacy> to convert.
     * @return A String representation of the ArrayList.
     */
    @TypeConverter
    fun fromArrayList(list: ArrayList<DurationIntervalLegacy>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}