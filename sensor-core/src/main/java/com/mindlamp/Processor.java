
package com.mindlamp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SyncRequest;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mindlamp.providers.Processor_Provider;
import com.mindlamp.providers.Processor_Provider.Processor_Data;
import com.mindlamp.utils.Lamp_Sensor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Service that logs CPU activity on the device
 *
 * @author df
 */
public class Processor extends Lamp_Sensor {

    /**
     * Broadcasted event: when there is new processor usage information
     */
    public static final String ACTION_LAMP_PROCESSOR = "ACTION_LAMP_PROCESSOR";

    /**
     * Broadcasted event: fired when the processor idle is below 10%
     */
    public static final String ACTION_LAMP_PROCESSOR_STRESSED = "ACTION_LAMP_PROCESSOR_STRESSED";

    /**
     * Broadcasted event: fired when the processor idle is above 90%
     */
    public static final String ACTION_LAMP_PROCESSOR_RELAXED = "ACTION_LAMP_PROCESSOR_RELAXED";

    private static Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            HashMap<String, Integer> processorNow = getProcessorLoad();

            float user_percentage = 0, system_percentage = 0, idle_percentage = 0;

            Cursor lastProcessor = getContentResolver().query(Processor_Data.CONTENT_URI, null, null, null, Processor_Data.TIMESTAMP + " DESC LIMIT 1");
            if (lastProcessor != null && lastProcessor.moveToFirst()) {
                int oldUser = lastProcessor.getInt(lastProcessor.getColumnIndex(Processor_Data.LAST_USER));
                int oldSystem = lastProcessor.getInt(lastProcessor.getColumnIndex(Processor_Data.LAST_SYSTEM));
                int oldIdle = lastProcessor.getInt(lastProcessor.getColumnIndex(Processor_Data.LAST_IDLE));

                int delta_user = processorNow.get("user") - oldUser;
                int delta_system = processorNow.get("system") - oldSystem;
                int delta_idle = processorNow.get("idle") - oldIdle;

                try {
                    user_percentage = delta_user * 100.f / (delta_user + delta_system + delta_idle);
                    system_percentage = delta_system * 100.f / (delta_user + delta_system + delta_idle);
                    idle_percentage = delta_idle * 100.f / (delta_user + delta_system + delta_idle);
                } catch (ArithmeticException e) {
                }
            }
            if (lastProcessor != null && !lastProcessor.isClosed()) lastProcessor.close();

            if (Lamp.DEBUG)
                Log.d(TAG, "USER: " + user_percentage + "% SYSTEM: " + system_percentage + "% IDLE: " + idle_percentage + "% Total: " + (user_percentage + system_percentage + idle_percentage));

            ContentValues rowData = new ContentValues();
            rowData.put(Processor_Data.TIMESTAMP, System.currentTimeMillis());
            rowData.put(Processor_Data.DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
            rowData.put(Processor_Data.LAST_USER, processorNow.get("user"));
            rowData.put(Processor_Data.LAST_SYSTEM, processorNow.get("system"));
            rowData.put(Processor_Data.LAST_IDLE, processorNow.get("idle"));
            rowData.put(Processor_Data.USER_LOAD, user_percentage);
            rowData.put(Processor_Data.SYSTEM_LOAD, system_percentage);
            rowData.put(Processor_Data.IDLE_LOAD, idle_percentage);

            try {
                getContentResolver().insert(Processor_Data.CONTENT_URI, rowData);

                if (awareSensor != null) awareSensor.onChanged(rowData);

            } catch (SQLiteException e) {
                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
            } catch (SQLException e) {
                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
            } catch (IllegalStateException e) {
                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
            }

            Intent newProcessor = new Intent(ACTION_LAMP_PROCESSOR);
            sendBroadcast(newProcessor);

            if (idle_percentage <= 10) {
                Intent stressed = new Intent(ACTION_LAMP_PROCESSOR_STRESSED);
                sendBroadcast(stressed);

                if (awareSensor != null) awareSensor.onOverloaded();
            }

            if (idle_percentage >= 90) {
                Intent relaxed = new Intent(ACTION_LAMP_PROCESSOR_RELAXED);
                sendBroadcast(relaxed);

                if (awareSensor != null) awareSensor.onIdle();
            }

            mHandler.postDelayed(mRunnable, Integer.parseInt(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR)) * 1000);
        }
    };

    private static int FREQUENCY = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static Processor.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Processor.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Processor.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        /**
         * CPU load is >=90%
         */
        void onOverloaded();

        /**
         * CPU load is <=10%
         */
        void onIdle();

        /**
         * CPU load updated
         *
         * @param data
         */
        void onChanged(ContentValues data);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AUTHORITY = Processor_Provider.getAuthority(this);

        if (Lamp.DEBUG) Log.d(TAG, "Processor service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d(TAG, "Processor service is not allowed by Google, buuuu. Disabling sensor...");

                Toast.makeText(getApplicationContext(), "Google has disabled processor sensor: Android N (7+).", Toast.LENGTH_LONG).show();

                Lamp.setSetting(getApplicationContext(), Lamp_Preferences.STATUS_PROCESSOR, false);
                Lamp.stopProcessor(getApplicationContext());
                stopSelf();
                return START_STICKY;
            }

            DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");
            if (Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR).length() == 0) {
                Lamp.setSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR, 10);
            }

            try {
                Integer.parseInt(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR));
            } catch (NumberFormatException e) {
                Lamp.setSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR, 10);
            }

            Lamp.setSetting(this, Lamp_Preferences.STATUS_PROCESSOR, true);
            if (FREQUENCY != Integer.parseInt(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR))) {
                mHandler.removeCallbacks(mRunnable);
                mHandler.post(mRunnable);
                FREQUENCY = Integer.parseInt(Lamp.getSetting(getApplicationContext(), Lamp_Preferences.FREQUENCY_PROCESSOR));
            }

            if (Lamp.DEBUG) Log.d(TAG, "Processor service active: " + FREQUENCY + "s");

            if (Lamp.isStudy(this)) {
                ContentResolver.setIsSyncable(Lamp.getLAMPAccount(this), Processor_Provider.getAuthority(this), 1);
                ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), Processor_Provider.getAuthority(this), true);
                long frequency = Long.parseLong(Lamp.getSetting(this, Lamp_Preferences.FREQUENCY_WEBSERVICE)) * 60;
                SyncRequest request = new SyncRequest.Builder()
                        .syncPeriodic(frequency, frequency / 3)
                        .setSyncAdapter(Lamp.getLAMPAccount(this), Processor_Provider.getAuthority(this))
                        .setExtras(new Bundle()).build();
                ContentResolver.requestSync(request);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(mRunnable);

        ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), Processor_Provider.getAuthority(this), false);
        ContentResolver.removePeriodicSync(
                Lamp.getLAMPAccount(this),
                Processor_Provider.getAuthority(this),
                Bundle.EMPTY
        );

        if (Lamp.DEBUG) Log.d(TAG, "Processor service terminated...");
    }

    /**
     * Get processor load from /proc/stat and returns an hashmap with the values:
     * [user]
     * [system]
     * [idle]
     *
     * @return {@link HashMap} with user, system and idle keys and values
     */
    public static HashMap<String, Integer> getProcessorLoad() {
        HashMap<String, Integer> processor = new HashMap<String, Integer>();
        processor.put("user", 0);
        processor.put("system", 0);
        processor.put("idle", 0);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 5000);
            String line = "";
            //NOTE: CPU  USER NICE SYSTEM IDLE - there are two spaces after CPU
            if ((line = reader.readLine()) != null) {
                String[] items = line.split(" ");
                processor.put("user", Integer.parseInt(items[2]) + Integer.parseInt(items[3]));
                processor.put("system", Integer.parseInt(items[4]));
                processor.put("idle", Integer.parseInt(items[5]));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processor;
    }
}
