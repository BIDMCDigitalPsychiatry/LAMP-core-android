package com.mindlamp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncRequest;
import android.os.Bundle;
import android.util.Log;

import com.mindlamp.providers.Keyboard_Provider;
import com.mindlamp.utils.Lamp_Sensor;

/**
 * Created by denzil on 23/10/14.
 */
public class Keyboard extends Lamp_Sensor {

    /**
     * Broadcasted event: keyboard input detected
     */
    public static final String ACTION_LAMP_KEYBOARD = "ACTION_LAMP_KEYBOARD";

    @Override
    public void onCreate() {
        super.onCreate();

        AUTHORITY = Keyboard_Provider.getAuthority(this);

        TAG = "LAMP::Keyboard";

        if (Lamp.DEBUG) Log.d(TAG, "Keyboard service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), Keyboard_Provider.getAuthority(this), false);
        ContentResolver.removePeriodicSync(
                Lamp.getLAMPAccount(this),
                Keyboard_Provider.getAuthority(this),
                Bundle.EMPTY
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            DEBUG = Lamp.getSetting(this, Lamp_Preferences.DEBUG_FLAG).equals("true");
            Lamp.setSetting(this, Lamp_Preferences.STATUS_KEYBOARD, true);

            if (Lamp.DEBUG) Log.d(TAG, "Keyboard service active...");

            if (Lamp.isStudy(this)) {
                ContentResolver.setIsSyncable(Lamp.getLAMPAccount(this), Keyboard_Provider.getAuthority(this), 1);
                ContentResolver.setSyncAutomatically(Lamp.getLAMPAccount(this), Keyboard_Provider.getAuthority(this), true);
                long frequency = Long.parseLong(Lamp.getSetting(this, Lamp_Preferences.FREQUENCY_WEBSERVICE)) * 60;
                SyncRequest request = new SyncRequest.Builder()
                        .syncPeriodic(frequency, frequency / 3)
                        .setSyncAdapter(Lamp.getLAMPAccount(this), Keyboard_Provider.getAuthority(this))
                        .setExtras(new Bundle()).build();
                ContentResolver.requestSync(request);
            }
        }

        return START_STICKY;
    }
}
