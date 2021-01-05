package digital.lamp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log
import digital.lamp.utils.LampConstants
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * WiFi Module. Scans and returns surrounding WiFi AccessPoints devices information and RSSI dB values.
 *
 * @author denzil
 */
class WiFi : Service() {
    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        wifiManager = this.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val filter = IntentFilter()
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiMonitor, filter)
        backgroundService = Intent(this, BackgroundService::class.java)
        backgroundService!!.action = ACTION_LAMP_WIFI_REQUEST_SCAN
        wifiScan = PendingIntent.getService(this, 0, backgroundService!!, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    interface LAMPSensorObserver {
        fun onWiFiAPDetected(data: ContentValues?)
        fun onWiFiDisabled()
        fun onWiFiScanStarted()
        fun onWiFiScanEnded()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (wifiManager == null) {
            stopSelf()
        } else {
            alarmManager!!.cancel(wifiScan)
            alarmManager!!.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, LampConstants.FREQUENCY_WIFI * 1000.toLong(), wifiScan)
            if (Lamp.DEBUG) Log.d(TAG, "WiFi service active...")
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiMonitor)
        if (wifiScan != null) alarmManager!!.cancel(wifiScan)
        if (Lamp.DEBUG) Log.d(TAG, "WiFi service terminated...")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    inner class WiFiMonitor : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val backgroundService = Intent(context, BackgroundService::class.java)
                backgroundService.action = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
                context.startService(backgroundService)
            }
        }
    }

    private val wifiMonitor = WiFiMonitor()

    /**
     * Asynchronously get the AP we are currently connected to.
     */
    private class WifiInfoFetch internal constructor(private val mContext: Context, private val mWifi: WifiInfo) : Callable<String> {
        @SuppressLint("HardwareIds")
        @Throws(Exception::class)
        override fun call(): String {
            val rowData = ContentValues()
            rowData.put(TIMESTAMP, System.currentTimeMillis())
            rowData.put(MAC_ADDRESS, mWifi.macAddress)
            rowData.put(BSSID, mWifi.bssid)
            rowData.put(SSID, mWifi.ssid)
            return Thread.currentThread().name
        }
    }

    /**
     * Asynchronously process the APs we can see around us
     */
    private class WifiApResults internal constructor(private val mContext: Context, private val mAPS: List<ScanResult>) : Callable<String> {
        @Throws(Exception::class)
        override fun call(): String {
            if (Lamp.DEBUG) Log.d(TAG, "Found " + mAPS.size + " access points")
            val currentScan = System.currentTimeMillis()
            for (ap in mAPS) {
                val rowData = ContentValues()
                rowData.put(TIMESTAMP, currentScan)
                rowData.put(BSSID, ap.BSSID)
                rowData.put(SSID, ap.SSID)
                rowData.put(SECURITY, ap.capabilities)
                rowData.put(FREQUENCY, ap.frequency)
                rowData.put(RSSI, ap.level)
                if (sensorObserver != null) sensorObserver!!.onWiFiAPDetected(rowData)
            }
            if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_WIFI_SCAN_ENDED)
            val scanEnd = Intent(ACTION_LAMP_WIFI_SCAN_ENDED)
            mContext.sendBroadcast(scanEnd)
            return Thread.currentThread().name
        }
    }

    /**
     * Background service for WiFi module
     * - ACTION_LAMP_WIFI_REQUEST_SCAN
     * - [WifiManager.SCAN_RESULTS_AVAILABLE_ACTION]
     * - ACTION_LAMP_WEBSERVICE
     *
     * @author df
     */
    class BackgroundService : IntentService(TAG + " background service") {
        override fun onHandleIntent(intent: Intent?) {
            if (intent!!.action != null) {
                val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                if (intent.action == ACTION_LAMP_WIFI_REQUEST_SCAN) {
                    try {
                        if (wifiManager.isWifiEnabled) {
                            if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_WIFI_SCAN_STARTED)
                            val scanStart = Intent(ACTION_LAMP_WIFI_SCAN_STARTED)
                            sendBroadcast(scanStart)
                            wifiManager.startScan()
                            if (sensorObserver != null) sensorObserver!!.onWiFiScanStarted()
                        } else {
                            if (Lamp.DEBUG) {
                                Log.d(TAG, "WiFi is off")
                            }
                            if (sensorObserver != null) sensorObserver!!.onWiFiDisabled()
                        }
                    } catch (e: NullPointerException) {
                        if (Lamp.DEBUG) {
                            Log.d(TAG, "WiFi is off")
                        }
                        if (sensorObserver != null) sensorObserver!!.onWiFiDisabled()
                    }
                }
                if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    val wifi = wifiManager.connectionInfo ?: return
                    val wifiInfo = WifiInfoFetch(applicationContext, wifi)
                    val scanResults = WifiApResults(applicationContext, wifiManager.scanResults)
                    val executor = Executors.newSingleThreadExecutor()
                    executor.submit(wifiInfo)
                    executor.submit(scanResults)
                    executor.shutdown()
                    if (sensorObserver != null) sensorObserver!!.onWiFiScanEnded()
                }
            }
        }
    }

    companion object {

        const val TIMESTAMP = "timestamp"
        const val BSSID = "bssid"
        const val SSID = "ssid"
        const val SECURITY = "security"
        const val FREQUENCY = "frequency"
        const val MAC_ADDRESS = "mac_address"
        const val RSSI = "rssi"

        private const val TAG = "LAMP::WiFi"
        private var alarmManager: AlarmManager? = null
        private var wifiManager: WifiManager? = null
        private var wifiScan: PendingIntent? = null
        private var backgroundService: Intent? = null

        /**
         * Broadcasted event: WiFi scan started
         */
        const val ACTION_LAMP_WIFI_SCAN_STARTED = "ACTION_LAMP_WIFI_SCAN_STARTED"

        /**
         * Broadcasted event: WiFi scan ended
         */
        const val ACTION_LAMP_WIFI_SCAN_ENDED = "ACTION_LAMP_WIFI_SCAN_ENDED"

        /**
         * Broadcast receiving event: request a WiFi scan
         */
        const val ACTION_LAMP_WIFI_REQUEST_SCAN = "ACTION_LAMP_WIFI_REQUEST_SCAN"
        var sensorObserver: LAMPSensorObserver? = null
    }
}