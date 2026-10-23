package digital.lamp.mindlamp.sensor.stream

/**
 * One accelerometer sample: Unix epoch milliseconds and m/s² in the Android axis convention.
 */
data class AccelRecord(val timestamp: Long, val x: Float, val y: Float, val z: Float) {
    companion object {
        /**
         * Wraps `SensorEvent.values` from `TYPE_ACCELEROMETER`. Android values are already m/s² in
         * the contract's axis convention, so they pass through unchanged.
         */
        fun fromSensorValues(timestamp: Long, values: FloatArray): AccelRecord =
            AccelRecord(timestamp, values[0], values[1], values[2])
    }
}
