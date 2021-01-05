package digital.lamp

import digital.lamp.Screen.ScreenMonitor
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.app.KeyguardManager
import android.app.Service
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log

/**
 * Service that logs users' interactions with the screen
 * - on/off events
 * - locked/unlocked events
 *
 * @author denzil
 */
class Screen : Service() {
    private var screenMonitor: ScreenMonitor? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    interface LAMPSensorObserver {
        fun onScreenOn()
        fun onScreenOff()
        fun onScreenLocked()
        fun onScreenUnlocked()
    }

    override fun onCreate() {
        super.onCreate()
        if (Lamp.DEBUG) Log.d(TAG, "Screen service created!")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (screenMonitor != null) unregisterReceiver(screenMonitor)
        if (Lamp.DEBUG) Log.d(TAG, "Screen service terminated...")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent.action != null) {
            val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (intent.action == ACTION_LAMP_SCREEN_ON) {
                if (sensorObserver != null) sensorObserver!!.onScreenOn()
                Log.d(TAG, ACTION_LAMP_SCREEN_ON)
                sendBroadcast(Intent(ACTION_LAMP_SCREEN_ON))
                if (km.isKeyguardLocked) {
                    if (sensorObserver != null) sensorObserver!!.onScreenUnlocked()
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED)
                    sendBroadcast(Intent(ACTION_LAMP_SCREEN_LOCKED))
                }
                return START_STICKY
            }
            if (intent.action == ACTION_LAMP_SCREEN_OFF) {
                if (sensorObserver != null) sensorObserver!!.onScreenOff()
                if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_OFF)
                sendBroadcast(Intent(ACTION_LAMP_SCREEN_OFF))
                if (km.isKeyguardLocked) {
                    if (sensorObserver != null) sensorObserver!!.onScreenLocked()
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED)
                    sendBroadcast(Intent(ACTION_LAMP_SCREEN_LOCKED))
                }
                return START_STICKY
            }
            if (intent.action == ACTION_LAMP_SCREEN_UNLOCKED) {
                if (!km.isKeyguardLocked) {
                    if (sensorObserver != null) sensorObserver!!.onScreenUnlocked()
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_UNLOCKED)
                    sendBroadcast(Intent(ACTION_LAMP_SCREEN_UNLOCKED))
                }
                return START_STICKY
            }
        }
        if (screenMonitor == null) {
            screenMonitor = ScreenMonitor()
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(Intent.ACTION_USER_PRESENT)
            filter.addAction(ACTION_LAMP_SCREEN_BOOT)
            registerReceiver(screenMonitor, filter)
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (pm.isInteractive) {
                if (sensorObserver != null) sensorObserver!!.onScreenOn()
                if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_ON)
                sendBroadcast(Intent(ACTION_LAMP_SCREEN_ON))
                if (km.isKeyguardLocked) {
                    if (sensorObserver != null) sensorObserver!!.onScreenUnlocked()
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED)
                    sendBroadcast(Intent(ACTION_LAMP_SCREEN_LOCKED))
                } else {
                    if (sensorObserver != null) sensorObserver!!.onScreenUnlocked()
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_UNLOCKED)
                    sendBroadcast(Intent(ACTION_LAMP_SCREEN_UNLOCKED))
                }
            } else {
                if (sensorObserver != null) sensorObserver!!.onScreenOff()
                if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_OFF)
                sendBroadcast(Intent(ACTION_LAMP_SCREEN_OFF))
                if (km.isKeyguardLocked) {
                    if (sensorObserver != null) sensorObserver!!.onScreenLocked()
                    if (Lamp.DEBUG) Log.d(TAG, ACTION_LAMP_SCREEN_LOCKED)
                    sendBroadcast(Intent(ACTION_LAMP_SCREEN_LOCKED))
                }
            }
        }
        if (Lamp.DEBUG) Log.d(TAG, "Screen service active...")
        return START_STICKY
    }

    private inner class ScreenMonitor : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                startService(Intent(context, Screen::class.java).setAction(ACTION_LAMP_SCREEN_ON))
            }
            if (intent.action == Intent.ACTION_SCREEN_OFF || !pm.isInteractive) {
                startService(Intent(context, Screen::class.java).setAction(ACTION_LAMP_SCREEN_OFF))
            }
            if (intent.action == Intent.ACTION_USER_PRESENT && !km.isKeyguardLocked) {
                startService(Intent(context, Screen::class.java).setAction(ACTION_LAMP_SCREEN_UNLOCKED))
            }
        }
    }

    companion object {
        private const val TAG = "LAMP::Screen"

        /**
         * Used to log the current screen status when starting the sensor
         */
        private const val ACTION_LAMP_SCREEN_BOOT = "ACTION_LAMP_SCREEN_BOOT"

        /**
         * Broadcasted event: screen is on
         */
        const val ACTION_LAMP_SCREEN_ON = "ACTION_LAMP_SCREEN_ON"

        /**
         * Broadcasted event: screen is off
         */
        const val ACTION_LAMP_SCREEN_OFF = "ACTION_LAMP_SCREEN_OFF"

        /**
         * Broadcasted event: screen is locked
         */
        const val ACTION_LAMP_SCREEN_LOCKED = "ACTION_LAMP_SCREEN_LOCKED"

        /**
         * Broadcasted event: screen is unlocked
         */
        const val ACTION_LAMP_SCREEN_UNLOCKED = "ACTION_LAMP_SCREEN_UNLOCKED"
        var sensorObserver: LAMPSensorObserver? = null
    }
}