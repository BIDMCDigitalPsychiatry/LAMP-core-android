package digital.lamp.mindlamp.sensor.stream

import android.content.Context
import androidx.work.WorkManager

/**
 * Stops sensor-stream collection and deletes its unsent data when the participant signs out,
 * as the legacy path drops its database tables.
 */
object SensorStreamLogout {
    /**
     * Stops both collectors, cancels both upload workers, and deletes every queued frame and
     * `lamp.gps1m` event. A frame that the stopping accelerometer seals afterwards stays in the
     * signed-out participant's own directory, so it's never uploaded for anyone else.
     */
    fun clear(context: Context) {
        val app = context.applicationContext
        HighRateAccelerometerCollector.stop()
        Gps1mCollector.stop()
        val workManager = WorkManager.getInstance(app)
        workManager.cancelUniqueWork(SensorStreamUploadWorker.WORK_NAME)
        workManager.cancelUniqueWork(Gps1mUploadWorker.WORK_NAME)
        SensorStreamSettings.setGps1mConfigured(app, false)
        SensorStreamFiles.clearAll(app.filesDir)
    }
}
