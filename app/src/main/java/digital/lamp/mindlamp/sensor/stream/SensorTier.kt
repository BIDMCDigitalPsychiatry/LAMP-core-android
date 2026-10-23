package digital.lamp.mindlamp.sensor.stream

/**
 * The four high-rate accelerometer sensor specs and their `SensorManager` settings.
 */
enum class SensorTier(val spec: String) {
    HZ10("lamp.accelerometer10hz"),
    HZ20("lamp.accelerometer20hz"),
    HZ50("lamp.accelerometer50hz"),
    HZ100("lamp.accelerometer100hz");

    val nominalHz: Int get() = TODO("not implemented")
    val samplingPeriodUs: Int get() = TODO("not implemented")
    val maxReportLatencyUs: Int get() = TODO("not implemented")
    val nominalSamplesPerWindow: Int get() = TODO("not implemented")

    companion object {
        const val MAX_REPORT_LATENCY_US = 5_000_000

        fun fromSpec(spec: String?): SensorTier? = TODO("not implemented")

        fun highestOf(specs: Collection<String?>): SensorTier? = TODO("not implemented")
    }
}
