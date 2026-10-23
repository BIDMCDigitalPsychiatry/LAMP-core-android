package digital.lamp.mindlamp.sensor.stream

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import digital.lamp.mindlamp.utils.LampLog
import java.io.File
import java.util.UUID

/**
 * Collects `TYPE_ACCELEROMETER` at one [SensorTier] and writes frames to a [FrameStore].
 *
 * The collector is a process-wide singleton. [start] is idempotent, and its lifecycle is
 * independent of `Lamp.stopLAMP`, which `LampForegroundService` calls every 5 minutes.
 */
object HighRateAccelerometerCollector {
    private const val TAG = "HighRateAccel"
    private val lock = Any()
    private var session: Session? = null

    /** Identifies this process; open frames of other processes are recovered on first start. */
    private val processId = UUID.randomUUID().toString().substring(0, 8)
    private var sessionCounter = 0
    private var recovered = false

    /** The tier being collected, or `null` when stopped. */
    val runningTier: SensorTier?
        get() = synchronized(lock) { session?.tier }

    fun store(context: Context) = FrameStore(File(context.applicationContext.filesDir, "sensor_stream"))

    /** Starts collecting at [tier]. Does nothing if that tier is already running. */
    fun start(context: Context, tier: SensorTier) {
        synchronized(lock) {
            val current = session
            if (current != null && current.tier == tier) return
            current?.stop()
            val app = context.applicationContext
            val recover = !recovered
            recovered = true
            session = Session(app, tier, store(app), "$processId-${sessionCounter++}", processId, recover)
                .also { it.start() }
        }
    }

    /** Stops collecting. Waits for the sensor FIFO flush, then closes every open frame. */
    fun stop() {
        synchronized(lock) {
            session?.stop()
            session = null
        }
    }

    private class Session(
        private val context: Context,
        val tier: SensorTier,
        private val store: FrameStore,
        private val baseId: String,
        private val processId: String,
        private val recover: Boolean
    ) : SensorEventListener2 {
        private val thread = HandlerThread("lamp-accel-${tier.nominalHz}hz").apply { start() }
        private val handler = Handler(thread.looper)
        private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        private val clock = SensorClock({ System.currentTimeMillis() }, { SystemClock.elapsedRealtimeNanos() })
        private val compressor = CodecSelector.forDevice()
        private val pending = ArrayList<AccelRecord>()
        private var sessionId = baseId
        private var partitioner = newPartitioner()
        private var sealedAny = false
        private var finished = false

        private val drain = object : Runnable {
            override fun run() {
                drainPending()
                handler.postDelayed(this, DRAIN_INTERVAL_MS)
            }
        }

        fun start() {
            handler.post {
                if (recover) recoverOrphans()
                clock.startSession()
                // A wake-up sensor wakes the CPU to deliver its FIFO, so batches survive doze.
                val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER, true)
                    ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                if (sensor == null) {
                    LampLog.e(TAG, "no accelerometer")
                    return@post
                }
                val ok = sensorManager.registerListener(
                    this, sensor, tier.samplingPeriodUs, tier.maxReportLatencyUs, handler
                )
                LampLog.e(TAG, "registered ${tier.spec} on ${sensor.name} (wakeUp=${sensor.isWakeUpSensor}): $ok")
                handler.postDelayed(drain, DRAIN_INTERVAL_MS)
            }
        }

        fun stop() {
            handler.post {
                // onFlushCompleted finishes the session; the timeout covers devices that never call it.
                if (!sensorManager.flush(this)) finish()
                handler.postDelayed({ finish() }, FLUSH_TIMEOUT_MS)
            }
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (finished || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
            pending.add(AccelRecord.fromSensorValues(clock.toEpochMs(event.timestamp), event.values))
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

        override fun onFlushCompleted(sensor: Sensor?) {
            handler.post { finish() }
        }

        private fun finish() {
            if (finished) return
            finished = true
            // An exception here would escape the HandlerThread and kill the process. A frame
            // that can't be sealed keeps its raw file, which a later process recovers.
            try {
                sensorManager.unregisterListener(this)
                handler.removeCallbacks(drain)
                drainPending()
                partitioner.closeAll()
                if (sealedAny) SensorStreamUploadWorker.enqueue(context)
            } catch (e: Throwable) {
                LampLog.e(TAG, "finish failed: ${e.message}")
            } finally {
                thread.quitSafely()
            }
        }

        private fun drainPending() {
            try {
                if (pending.isNotEmpty()) {
                    val batch = ArrayList(pending)
                    pending.clear()
                    partitioner.accept(batch)
                }
                partitioner.closeExpired(System.currentTimeMillis())
                if (clock.hasDrifted()) {
                    // The wall clock jumped: close everything and start a new clock session.
                    partitioner.closeAll()
                    clock.startSession()
                    sessionId = "$baseId-${clock.sessionCount}"
                    partitioner = newPartitioner()
                }
                if (sealedAny && !finished) {
                    sealedAny = false
                    SensorStreamUploadWorker.enqueue(context)
                }
            } catch (e: Exception) {
                LampLog.e(TAG, "drain failed: ${e.message}")
            }
        }

        private fun newPartitioner() = FramePartitioner(object : FramePartitioner.Listener {
            override fun onRecordsAppended(key: FramePartitioner.FrameKey, records: List<AccelRecord>) {
                store.appendOpen(tier.spec, sessionId, key, records)
            }

            override fun onFrameClosed(frame: FramePartitioner.ClosedFrame) {
                sealedAny = true
                try {
                    store.seal(tier.spec, sessionId, frame, compressor)
                } catch (e: Exception) {
                    LampLog.e(TAG, "${compressor.codec} seal failed, using zstd: ${e.message}")
                    store.seal(tier.spec, sessionId, frame, ZstdCompressor())
                }
            }

            override fun onFrameCloseFailed(frame: FramePartitioner.ClosedFrame, error: Exception) {
                // The raw file stays in open/, so recovery seals it later.
                LampLog.e(TAG, "frame ${frame.key} not sealed: ${error.message}")
            }
        })

        private fun recoverOrphans() {
            try {
                val frames = store.recoverOpenFrames(processId, compressor)
                if (frames.isNotEmpty()) sealedAny = true
            } catch (e: Exception) {
                LampLog.e(TAG, "recovery failed: ${e.message}")
            }
        }
    }

    private const val DRAIN_INTERVAL_MS = 1_000L
    private const val FLUSH_TIMEOUT_MS = 3_000L
}
