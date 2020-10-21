
package com.mindlamp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncRequest;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mindlamp.providers.TimeZone_Provider;
import com.mindlamp.providers.TimeZone_Provider.TimeZone_Data;
import com.mindlamp.utils.Lamp_Sensor;

import java.util.TimeZone;

/**
 * Timezone module. Keeps track of changes in the device Timezone.
 * Last modified:
 *
 * @author Denzil
 *         Made sensor event-based, instead of polling data.
 *         <p>
 *         Original @author Nikola
 */
public class Timezone extends Lamp_Sensor {

    /**
     * Broadcasted event: when there is new timezone information
     */
    public static final String ACTION_LAMP_TIMEZONE = "ACTION_LAMP_TIMEZONE";
    public static final String EXTRA_DATA = "data";

    private static Timezone.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(Timezone.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static Timezone.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onTimezoneChanged(ContentValues data);
    }

    private String lastTimezone = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = TimeZone_Provider.getAuthority(this);

        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                Intent newTimeZone = new Intent(ACTION_LAMP_TIMEZONE);
                sendBroadcast(newTimeZone);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(timezoneObserver, filter);

        if (Lamp.DEBUG) Log.d(TAG, "Timezone service created");
    }

    private TimezoneObserver timezoneObserver = new TimezoneObserver();

    public class TimezoneObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_TIMEZONE_CHANGED)) {
                retrieveTimezone();
            }
        }
    }

    /**
     * Logs the current timezone
     */
    private void retrieveTimezone() {
        if (lastTimezone.equalsIgnoreCase(TimeZone.getDefault().getID())) return;

        lastTimezone = TimeZone.getDefault().getID();
        ContentValues rowData = new ContentValues();
        rowData.put(TimeZone_Data.TIMESTAMP, System.currentTimeMillis());
        rowData.put(TimeZone_Data.DEVICE_ID, Lamp.getSetting(getApplicationContext(), Lamp_Preferences.DEVICE_ID));
        rowData.put(TimeZone_Data.TIMEZONE, lastTimezone);

        try {
            getContentResolver().insert(TimeZone_Data.CONTENT_URI, rowData);

            if (Lamp.DEBUG) Log.d(Lamp.TAG, rowData.toString());

            if (awareSensor != null) awareSensor.onTimezoneChanged(rowData);

            Intent newTimeZone = new Intent(ACTION_LAMP_TIMEZONE);
            newTimeZone.putExtra(EXTRA_DATA, rowData);
            sendBroadcast(newTimeZone);

        } catch (SQLiteException e) {
            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
        } catch (SQLException e) {
            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");
            Lamp.setSetting(this, Lamp_Preferences.STATUS_TIMEZONE, true);

            if (lastTimezone.length() == 0) retrieveTimezone();

            if (Lamp.DEBUG) Log.d(TAG, "Timezone service active...");

            if (Lamp.isStudy(this)) {
                ContentResolver.setIsSyncable(Lamp.getLAMPAccount(this), TimeZone_Provider.getAuthority(this), 1);
                ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), TimeZone_Provider.getAuthority(this), true);
                long frequency = Long.parseLong(Lamp.getSetting(this, Lamp_Preferences.FREQUENCY_WEBSERVICE)) * 60;
                SyncRequest request = new SyncRequest.Builder()
                        .syncPeriodic(frequency, frequency / 3)
                        .setSyncAdapter(Lamp.getLAMPAccount(this), TimeZone_Provider.getAuthority(this))
                        .setExtras(new Bundle()).build();
                ContentResolver.requestSync(request);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(timezoneObserver);

        ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), TimeZone_Provider.getAuthority(this), false);
        ContentResolver.removePeriodicSync(
                Lamp.getLAMPAccount(this),
                TimeZone_Provider.getAuthority(this),
                Bundle.EMPTY
        );

        if (Lamp.DEBUG) Log.d(TAG, "Timezone service terminated...");
    }
}
