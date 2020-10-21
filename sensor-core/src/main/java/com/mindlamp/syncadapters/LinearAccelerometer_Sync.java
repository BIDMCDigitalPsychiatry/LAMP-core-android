package com.mindlamp.syncadapters;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.mindlamp.providers.Linear_Accelerometer_Provider;

/**
 * Created by denzil on 22/07/2017.
 */

public class LinearAccelerometer_Sync extends Service {
    private LampSyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new LampSyncAdapter(getApplicationContext(), true, true);
                sSyncAdapter.init(
                        Linear_Accelerometer_Provider.DATABASE_TABLES,
                        Linear_Accelerometer_Provider.TABLES_FIELDS,
                        new Uri[]{
                                Linear_Accelerometer_Provider.Linear_Accelerometer_Sensor.CONTENT_URI,
                                Linear_Accelerometer_Provider.Linear_Accelerometer_Data.CONTENT_URI
                        });
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
