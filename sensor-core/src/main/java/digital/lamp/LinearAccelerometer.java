
package digital.lamp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.util.Log;
import digital.lamp.utils.LampConstants;
import digital.lamp.utils.Lamp_Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author df
 */
public class LinearAccelerometer extends Lamp_Sensor implements SensorEventListener {

    /**
     * Logging tag (default = "LAMP::LinearAcc.")
     */
    private static String TAG = "LAMP::Linear Acc.";

    private static SensorManager mSensorManager;
    private static Sensor mLinearAccelerator;

    private static HandlerThread sensorThread = null;
    private static Handler sensorHandler = null;
    private static PowerManager.WakeLock wakeLock = null;

    private static Float[] LAST_VALUES = null;
    private static long LAST_TS = 0;
    private static long LAST_SAVE = 0;

    private static int FREQUENCY = -1;
    private static double THRESHOLD = 0;
    // Reject any data points that come in more often than frequency
    private static boolean ENFORCE_FREQUENCY = false;

    /**
     * Broadcasted event: new sensor values
     * ContentProvider: LinearAccelerationProvider
     */
    public static final String ACTION_LAMP_LINEAR_ACCELEROMETER = "ACTION_LAMP_LINEAR_ACCELEROMETER";
    public static final String ACTION_LAMP_LINEAR_LABEL = "ACTION_LAMP_LINEAR_LABEL";
    public static final String EXTRA_LABEL = "label";

    /**
     * Until today, no available Android phone samples higher than 208Hz (Nexus 7).
     * http://ilessendata.blogspot.com/2012/11/android-accelerometer-sampling-rates.html
     */
    private List<ContentValues> data_values = new ArrayList<ContentValues>();

    private static String LABEL = "";

    private static DataLabel dataLabeler = new DataLabel();

    public static class DataLabel extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LAMP_LINEAR_LABEL)) {
                LABEL = intent.getStringExtra(EXTRA_LABEL);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //We log current accuracy on the sensor changed event
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (SignificantMotion.isSignificantMotionActive && !SignificantMotion.CURRENT_SIGMOTION_STATE) {
            if (data_values.size() > 0) {
                final ContentValues[] data_buffer = new ContentValues[data_values.size()];
                data_values.toArray(data_buffer);
                data_values.clear();
            }

            return;
        }

        long TS = System.currentTimeMillis();
        if ((TS - LAST_TS) < LampConstants.INTERVAL)
            return;
        if (LAST_VALUES != null && THRESHOLD > 0 && Math.abs(event.values[0] - LAST_VALUES[0]) < THRESHOLD
                && Math.abs(event.values[1] - LAST_VALUES[1]) < THRESHOLD
                && Math.abs(event.values[2] - LAST_VALUES[2]) < THRESHOLD) {
            return;
        }

        LAST_VALUES = new Float[]{event.values[0], event.values[1], event.values[2]};

        ContentValues rowData = new ContentValues();
        rowData.put(Linear_Accelerometer_Data.TIMESTAMP, TS);
        rowData.put(Linear_Accelerometer_Data.VALUES_0, event.values[0]);
        rowData.put(Linear_Accelerometer_Data.VALUES_1, event.values[1]);
        rowData.put(Linear_Accelerometer_Data.VALUES_2, event.values[2]);
        rowData.put(Linear_Accelerometer_Data.ACCURACY, event.accuracy);
        rowData.put(Linear_Accelerometer_Data.LABEL, LABEL);

        data_values.add(rowData);
        LAST_TS = TS;

        if (awareSensor != null) awareSensor.onLinearAccelChanged(rowData);

        if (data_values.size() < 250 && TS < LAST_SAVE + 300000) {
            return;
        }

        final ContentValues[] data_buffer = new ContentValues[data_values.size()];
        data_values.toArray(data_buffer);

        data_values.clear();
        LAST_SAVE = TS;
    }

    private static LinearAccelerometer.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(LinearAccelerometer.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static LinearAccelerometer.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onLinearAccelChanged(ContentValues data);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = getPackageName() + ".provider.accelerometer.linear";

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLinearAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorThread = new HandlerThread(TAG);
        sensorThread.start();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        sensorHandler = new Handler(sensorThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LAMP_LINEAR_LABEL);
        registerReceiver(dataLabeler, filter);

        if (Lamp.DEBUG) Log.d(TAG, "Linear-accelerometer service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorHandler.removeCallbacksAndMessages(null);
        mSensorManager.unregisterListener(this, mLinearAccelerator);
        sensorThread.quit();

        wakeLock.release();

        unregisterReceiver(dataLabeler);

        if (Lamp.DEBUG) Log.d(TAG, "Linear-accelerometer service terminated...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            if (mLinearAccelerator == null) {
                stopSelf();
            } else {


                int new_frequency = LampConstants.FREQUENCY_ACCELEROMETER;
                double new_threshold = LampConstants.THRESHOLD_ACCELEROMETER;

                if (FREQUENCY != new_frequency
                        || THRESHOLD != new_threshold) {

                    sensorHandler.removeCallbacksAndMessages(null);
                    mSensorManager.unregisterListener(this, mLinearAccelerator);

                    FREQUENCY = new_frequency;
                    THRESHOLD = new_threshold;
                }

                mSensorManager.registerListener(this, mLinearAccelerator, new_frequency, sensorHandler);
                LAST_SAVE = System.currentTimeMillis();

                if (Lamp.DEBUG)
                    Log.d(TAG, "Linear-accelerometer service active: " + FREQUENCY + "ms");
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final class Linear_Accelerometer_Data implements BaseColumns {

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String VALUES_0 = "double_values_0";
        public static final String VALUES_1 = "double_values_1";
        public static final String VALUES_2 = "double_values_2";
        public static final String ACCURACY = "accuracy";
        public static final String LABEL = "label";
    }
}