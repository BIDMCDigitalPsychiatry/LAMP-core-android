package digital.lamp.mindlamp.sensor.stream

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/** Loads the golden sensor-stream vectors copied from LAMP-protocol. */
object Vectors {
    fun bytes(name: String): ByteArray {
        val stream = Vectors::class.java.classLoader!!.getResourceAsStream("sensor-stream/$name")
            ?: error("missing test vector $name")
        return stream.use { it.readBytes() }
    }

    fun json(name: String) = JsonParser().parse(String(bytes(name), Charsets.UTF_8))

    fun records(): List<AccelRecord> = json("records.json").asJsonArray.map {
        val o = it.asJsonObject
        AccelRecord(
            o["timestamp"].asLong,
            o["x"].asDouble.toFloat(),
            o["y"].asDouble.toFloat(),
            o["z"].asDouble.toFloat()
        )
    }

    fun edgeCases(): JsonObject = json("edge_cases.json").asJsonObject

    fun meta(): JsonObject = json("meta.json").asJsonObject

    fun recordRows(array: JsonArray): List<AccelRecord> = array.map {
        val row = it.asJsonArray
        AccelRecord(
            row[0].asLong,
            row[1].asDouble.toFloat(),
            row[2].asDouble.toFloat(),
            row[3].asDouble.toFloat()
        )
    }

    fun hex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
}
