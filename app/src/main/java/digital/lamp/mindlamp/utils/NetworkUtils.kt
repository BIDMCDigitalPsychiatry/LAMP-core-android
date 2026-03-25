package digital.lamp.mindlamp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build

/**
 * Utility class responsible for checking device network connection,
 * Wi-Fi status, and battery percentage.
 */
object NetworkUtils {

    /**
     * Check whether any network connection (mobile data or Wi-Fi) is available
     * and has validated internet access.
     *
     * @param context Application or Activity context
     * @return true if internet is available, false otherwise
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivity.activeNetwork ?: return false
            val capabilities =
                connectivity.getNetworkCapabilities(network) ?: return false
            // NET_CAPABILITY_INTERNET   → network is supposed to have internet
            // NET_CAPABILITY_VALIDATED  → Android confirmed real traffic can flow
            //                            (catches captive portals like hotel Wi-Fi)
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            // Fallback for API < 23 — deprecated but still functional
            @Suppress("DEPRECATION")
            val netInfo = connectivity.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            netInfo.isConnected
        }
    }

    /**
     * Check whether the device is connected to a Wi-Fi access point.
     *
     * Uses NetworkCapabilities on API 29+ to avoid deprecated WifiManager APIs.
     *
     * @param context Application or Activity context
     * @return true if connected to Wi-Fi, false otherwise
     */
    fun isWifiNetworkAvailable(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivity.activeNetwork ?: return false
            val capabilities =
                connectivity.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            // Fallback for API < 23
            @Suppress("DEPRECATION")
            val wifiMgr =
                context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            if (wifiMgr.isWifiEnabled) {
                @Suppress("DEPRECATION")
                wifiMgr.connectionInfo.networkId != -1
            } else {
                false
            }
        }
    }

    /**
     * Fetch the current device battery percentage.
     *
     * Uses BatteryManager directly on API 21+, falls back to
     * broadcast receiver for older devices.
     *
     * @param context Application or Activity context
     * @return Battery level as an integer (0–100), or -1 if unavailable
     */
    fun getBatteryPercentage(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            // Fallback for API < 21 — use sticky broadcast
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, iFilter)

            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            if (level == -1 || scale == -1) return -1
            ((level / scale.toDouble()) * 100).toInt()
        }
    }
}