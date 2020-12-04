
package com.mindlamp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.util.Log;
import com.mindlamp.utils.LampConstants;
import com.mindlamp.utils.Lamp_Sensor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WiFi Module. Scans and returns surrounding WiFi AccessPoints devices information and RSSI dB values.
 *
 * @author denzil
 */
public class WiFi extends Lamp_Sensor {

    private static String TAG = "LAMP::WiFi";

    private static AlarmManager alarmManager = null;
    private static WifiManager wifiManager = null;
    private static PendingIntent wifiScan = null;
    private static Intent backgroundService = null;


    /**
     * Broadcasted event: WiFi scan started
     */
    public static final String ACTION_LAMP_WIFI_SCAN_STARTED = "ACTION_LAMP_WIFI_SCAN_STARTED";

    /**
     * Broadcasted event: WiFi scan ended
     */
    public static final String ACTION_LAMP_WIFI_SCAN_ENDED = "ACTION_LAMP_WIFI_SCAN_ENDED";

    /**
     * Broadcast receiving event: request a WiFi scan
     */
    public static final String ACTION_LAMP_WIFI_REQUEST_SCAN = "ACTION_LAMP_WIFI_REQUEST_SCAN";

    @Override
    public void onCreate() {
        super.onCreate();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiMonitor, filter);

        backgroundService = new Intent(this, BackgroundService.class);
        backgroundService.setAction(ACTION_LAMP_WIFI_REQUEST_SCAN);
        wifiScan = PendingIntent.getService(this, 0, backgroundService, PendingIntent.FLAG_UPDATE_CURRENT);

        REQUIRED_PERMISSIONS.add(Manifest.permission.CHANGE_WIFI_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_WIFI_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_NETWORK_STATE);
    }

    private static WiFi.LAMPSensorObserver awareSensor;

    public static void setSensorObserver(WiFi.LAMPSensorObserver observer) {
        awareSensor = observer;
    }

    public static WiFi.LAMPSensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface LAMPSensorObserver {
        void onWiFiAPDetected(ContentValues data);

        void onWiFiDisabled();

        void onWiFiScanStarted();

        void onWiFiScanEnded();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            if (wifiManager == null) {
                stopSelf();
            } else {

                alarmManager.cancel(wifiScan);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, LampConstants.FREQUENCY_WIFI * 1000, wifiScan);

                if (Lamp.DEBUG) Log.d(TAG, "WiFi service active...");
            }

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(wifiMonitor);
        if (wifiScan != null) alarmManager.cancel(wifiScan);

        if (Lamp.DEBUG) Log.d(TAG, "WiFi service terminated...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class WiFiMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Intent backgroundService = new Intent(context, BackgroundService.class);
                backgroundService.setAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                context.startService(backgroundService);
            }
        }
    }

    private final WiFiMonitor wifiMonitor = new WiFiMonitor();

    /**
     * Asynchronously get the AP we are currently connected to.
     */
    private static class WifiInfoFetch implements Callable<String> {
        private Context mContext;
        private WifiInfo mWifi;

        WifiInfoFetch(Context c, WifiInfo w) {
            mContext = c;
            mWifi = w;
        }

        @Override
        public String call() throws Exception {
            ContentValues rowData = new ContentValues();
            rowData.put(WiFi_Data.TIMESTAMP, System.currentTimeMillis());
            rowData.put(WiFi_Data.MAC_ADDRESS,  mWifi.getMacAddress());
            rowData.put(WiFi_Data.BSSID,  mWifi.getBSSID());
            rowData.put(WiFi_Data.SSID,  mWifi.getSSID());

//            try {
////                mContext.getContentResolver().insert(WiFi_Sensor.CONTENT_URI, rowData);
//
//                Intent currentAp = new Intent(ACTION_LAMP_WIFI_CURRENT_AP);
//                currentAp.putExtra(EXTRA_DATA, rowData);
//                mContext.sendBroadcast(currentAp);
//
//                if (Lamp.DEBUG) Log.d(TAG, "WiFi local sensor information: " + rowData.toString());
//
//            } catch (SQLiteException e) {
//                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//            } catch (SQLException e) {
//                if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//            }

            return Thread.currentThread().getName();
        }
    }

    /**
     * Asynchronously process the APs we can see around us
     */
    private static class WifiApResults implements Callable<String> {
        private Context mContext;
        private List<ScanResult> mAPS;

        WifiApResults(Context c, List<ScanResult> aps) {
            mContext = c;
            mAPS = aps;
        }

        @Override
        public String call() throws Exception {
            if (Lamp.DEBUG) Log.d(TAG, "Found " + mAPS.size() + " access points");
            long currentScan = System.currentTimeMillis();

            for (ScanResult ap : mAPS) {
                ContentValues rowData = new ContentValues();
                rowData.put(WiFi_Data.TIMESTAMP, currentScan);
                rowData.put(WiFi_Data.BSSID, ap.BSSID);
                rowData.put(WiFi_Data.SSID, ap.SSID);
                rowData.put(WiFi_Data.SECURITY, ap.capabilities);
                rowData.put(WiFi_Data.FREQUENCY, ap.frequency);
                rowData.put(WiFi_Data.RSSI, ap.level);

//                try {
////                    mContext.getContentResolver().insert(WiFi_Data.CONTENT_URI, rowData);
//
                    if (awareSensor != null) awareSensor.onWiFiAPDetected(rowData);
//
//                    if (Lamp.DEBUG)
//                        Log.d(TAG, ACTION_LAMP_WIFI_NEW_DEVICE + ": " + rowData.toString());
//
//                    Intent detectedAP = new Intent(ACTION_LAMP_WIFI_NEW_DEVICE);
//                    detectedAP.putExtra(EXTRA_DATA, rowData);
//                    mContext.sendBroadcast(detectedAP);
//
//                } catch (SQLiteException e) {
//                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//                } catch (SQLException e) {
//                    if (Lamp.DEBUG) Log.d(TAG, e.getMessage());
//                }
            }

            if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_WIFI_SCAN_ENDED);

            Intent scanEnd = new Intent(ACTION_LAMP_WIFI_SCAN_ENDED);
            mContext.sendBroadcast(scanEnd);

            return Thread.currentThread().getName();
        }
    }

    /**
     * Background service for WiFi module
     * - ACTION_LAMP_WIFI_REQUEST_SCAN
     * - {@link WifiManager#SCAN_RESULTS_AVAILABLE_ACTION}
     * - ACTION_LAMP_WEBSERVICE
     *
     * @author df
     */
    public static class BackgroundService extends IntentService {
        public BackgroundService() {
            super(TAG + " background service");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent.getAction() != null) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                if (intent.getAction().equals(WiFi.ACTION_LAMP_WIFI_REQUEST_SCAN)) {
                    try {
                        if (wifiManager.isWifiEnabled()) {

                            if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_WIFI_SCAN_STARTED);

                            Intent scanStart = new Intent(ACTION_LAMP_WIFI_SCAN_STARTED);
                            sendBroadcast(scanStart);

                            wifiManager.startScan();

                            if (awareSensor != null) awareSensor.onWiFiScanStarted();

                        } else {
                            if (Lamp.DEBUG) {
                                Log.d(WiFi.TAG, "WiFi is off");
                            }

//                            ContentValues rowData = new ContentValues();
//                            rowData.put(WiFi_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                            rowData.put(WiFi_Data.TIMESTAMP, System.currentTimeMillis());
//                            rowData.put(WiFi_Data.LABEL, "disabled");
//
//                            getContentResolver().insert(WiFi_Data.CONTENT_URI, rowData);

                            if (awareSensor != null) awareSensor.onWiFiDisabled();
                        }
                    } catch (NullPointerException e) {
                        if (Lamp.DEBUG) {
                            Log.d(WiFi.TAG, "WiFi is off");
                        }

//                        ContentValues rowData = new ContentValues();
//                        rowData.put(WiFi_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//                        rowData.put(WiFi_Data.TIMESTAMP, System.currentTimeMillis());
//                        rowData.put(WiFi_Data.LABEL, "disabled");
//
//                        getContentResolver().insert(WiFi_Data.CONTENT_URI, rowData);

                        if (awareSensor != null) awareSensor.onWiFiDisabled();
                    }
                }

                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    WifiInfo wifi = wifiManager.getConnectionInfo();
                    if (wifi == null) return;

                    WifiInfoFetch wifiInfo = new WifiInfoFetch(getApplicationContext(), wifi);
                    WifiApResults scanResults = new WifiApResults(getApplicationContext(), wifiManager.getScanResults());

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(wifiInfo);
                    executor.submit(scanResults);
                    executor.shutdown();

                    if (awareSensor != null) awareSensor.onWiFiScanEnded();
                }
            }
        }
    }

    public static final class WiFi_Data implements BaseColumns {

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String BSSID = "bssid";
        public static final String SSID = "ssid";
        public static final String SECURITY = "security";
        public static final String FREQUENCY = "frequency";
        public static final String MAC_ADDRESS = "mac_address";
        public static final String RSSI = "rssi";
        public static final String LABEL = "label";
    }
}
