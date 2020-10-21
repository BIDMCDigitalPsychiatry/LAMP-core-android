package com.mindlamp.syncadapters;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.mindlamp.providers.Applications_Provider;

/**
 * Created by denzil on 18/07/2017.
 */
public class Applications_Sync extends Service {

    private LampSyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new LampSyncAdapter(getApplicationContext(), true, true);
                sSyncAdapter.init(
                        Applications_Provider.DATABASE_TABLES,
                        Applications_Provider.TABLES_FIELDS,
                        new Uri[]{
                                Applications_Provider.Applications_Foreground.CONTENT_URI,
                                Applications_Provider.Applications_History.CONTENT_URI,
                                Applications_Provider.Applications_Notifications.CONTENT_URI,
                                Applications_Provider.Applications_Crashes.CONTENT_URI
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
