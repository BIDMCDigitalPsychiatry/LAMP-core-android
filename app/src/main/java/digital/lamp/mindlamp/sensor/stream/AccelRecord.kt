package digital.lamp.mindlamp.sensor.stream

/**
 * One accelerometer sample: Unix epoch milliseconds and m/s² in the Android axis convention.
 */
data class AccelRecord(val timestamp: Long, val x: Float, val y: Float, val z: Float) {
    companion object {
        fun fromSensorValues(timestamp: Long, values: FloatArray): AccelRecord = TODO("not implemented")
    }
}
