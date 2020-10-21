package com.mindlamp.syncadapters;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.mindlamp.providers.Lamp_Provider;

/**
 * Created by denzil on 22/07/2017.
 */

public class Lamp_Sync extends Service {
    private LampSyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new LampSyncAdapter(getApplicationContext(), true, true);
                sSyncAdapter.init(
                        new String[]{Lamp_Provider.DATABASE_TABLES[0], Lamp_Provider.DATABASE_TABLES[3], Lamp_Provider.DATABASE_TABLES[4]},
                        new String[]{Lamp_Provider.TABLES_FIELDS[0], Lamp_Provider.TABLES_FIELDS[3], Lamp_Provider.TABLES_FIELDS[4]},
                        new Uri[]{Lamp_Provider.Aware_Device.CONTENT_URI, Lamp_Provider.Aware_Studies.CONTENT_URI, Lamp_Provider.Aware_Log.CONTENT_URI}
                );
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
