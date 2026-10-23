package digital.lamp.mindlamp.sensor.stream

/**
 * The four high-rate accelerometer sensor specs and their `SensorManager` settings.
 *
 * Every tier batches samples in the hardware FIFO for up to [MAX_REPORT_LATENCY_US], so the
 * app wakes about every 5 s instead of once per sample.
 */
enum class SensorTier(val spec: String, val nominalHz: Int) {
    HZ10("lamp.accelerometer10hz", 10),
    HZ20("lamp.accelerometer20hz", 20),
    HZ50("lamp.accelerometer50hz", 50),
    HZ100("lamp.accelerometer100hz", 100);

    /** The `samplingPeriodUs` argument for `SensorManager.registerListener`. */
    val samplingPeriodUs: Int get() = 1_000_000 / nominalHz

    /** The `maxReportLatencyUs` argument for `SensorManager.registerListener`. */
    val maxReportLatencyUs: Int get() = MAX_REPORT_LATENCY_US

    /** Samples in one full 5-minute window at the nominal rate. */
    val nominalSamplesPerWindow: Int get() = nominalHz * (FrameWindow.WINDOW_MS / 1000L).toInt()

    companion object {
        const val MAX_REPORT_LATENCY_US = 5_000_000

        /** Returns the tier for [spec], or `null` when [spec] isn't a high-rate accelerometer spec. */
        fun fromSpec(spec: String?): SensorTier? = values().firstOrNull { it.spec == spec }

        /** Returns the fastest configured tier, because one collector serves the device. */
        fun highestOf(specs: Collection<String?>): SensorTier? =
            specs.mapNotNull { fromSpec(it) }.maxByOrNull { it.nominalHz }
    }
}
