package digital.lamp.mindlamp.sensor.health_services

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import digital.lamp.mindlamp.app.App
import lamp.mindlamp.sensormodule.aware.aware.model.SensorEventData
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type


object SensorStore {

    fun getStoredSensorValues(): ArrayList<SensorEventData> {
        val gson = Gson()
        val file = File(App.app.filesDir, "sensors.txt")
        val list: ArrayList<SensorEventData>
        if (file.length() > 0) {

            val reader = JsonReader(FileReader(file))
            val REVIEW_TYPE: Type =
                object : TypeToken<List<SensorEventData?>?>() {}.getType()
            list = gson.fromJson(reader, REVIEW_TYPE)
            return list
        } else
            return ArrayList<SensorEventData>()

    }

    fun storeValue(sensorData: SensorEventData) {
        val gson = Gson()
        val file = File(App.app.filesDir, "sensors.txt")

        val list: ArrayList<SensorEventData> = if (file.length() > 0) {
            val reader = JsonReader(FileReader(file))
            val type: Type = object : TypeToken<List<SensorEventData?>?>() {}.type
            gson.fromJson(reader, type)

        } else {
            ArrayList<SensorEventData>()
        }

        list.add(sensorData)
        val sensorDataText = gson.toJson(list)
        clear()
        file.writeText(sensorDataText)

    }

    fun clear() {
        val file = File(App.app.filesDir, "sensors.txt")
        val fw: FileWriter
        try {
            fw = FileWriter(file)
            fw.write("")
            fw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
