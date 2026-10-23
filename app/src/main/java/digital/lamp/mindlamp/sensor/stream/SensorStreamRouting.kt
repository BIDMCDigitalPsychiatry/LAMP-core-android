package digital.lamp.mindlamp.sensor.stream

/**
 * Decides which accelerometer and location collectors run for a study's sensor specs, per
 * the contract's "Which sensors run together" rules.
 *
 * - When more than one `lamp.accelerometer*hz` tier is configured, only the fastest runs.
 * - When any tier is configured, the legacy 5 Hz `lamp.accelerometer` JSON stream doesn't run.
 *   `lamp.device_motion` is unaffected.
 * - `lamp.gps1m` runs alongside `lamp.gps`.
 */
class SensorStreamRouting private constructor(specs: Collection<String?>) {
    private val configured: Set<String> = specs.filterNotNull().toSet()
    private val tiers: List<SensorTier> = configured.mapNotNull { SensorTier.fromSpec(it) }.sortedBy { it.nominalHz }

    /** The tier to collect, or `null` when no tier is configured. */
    val accelerometerTier: SensorTier? = tiers.lastOrNull()

    /** Configured tiers that don't run because a faster tier is configured, slowest first. */
    val ignoredAccelerometerSpecs: List<String> = tiers.dropLast(1).map { it.spec }

    /** Whether the legacy 5 Hz `lamp.accelerometer` JSON stream runs. */
    val runsLegacyAccelerometer: Boolean = LEGACY_ACCELEROMETER in configured && accelerometerTier == null

    /** Whether the legacy `lamp.device_motion` stream runs. */
    val runsLegacyDeviceMotion: Boolean = DEVICE_MOTION in configured

    /** Whether the `lamp.gps1m` collector runs. */
    val runsGps1m: Boolean = Gps1mRequest.SPEC in configured

    /** The legacy specs that the 5 Hz accelerometer collector serves. */
    val legacyMotionSpecs: Set<String>
        get() = setOfNotNull(
            LEGACY_ACCELEROMETER.takeIf { runsLegacyAccelerometer },
            DEVICE_MOTION.takeIf { runsLegacyDeviceMotion }
        )

    companion object {
        const val LEGACY_ACCELEROMETER = "lamp.accelerometer"
        const val DEVICE_MOTION = "lamp.device_motion"

        fun from(specs: Collection<String?>): SensorStreamRouting = SensorStreamRouting(specs)
    }
}
