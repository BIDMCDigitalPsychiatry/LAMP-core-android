package digital.lamp.mindlamp.utils

import android.util.Log

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
internal object LampLog {

    val DOLOG = true
    private val TAG = "Lamp V2"


    /**
     * Error Log
     * @param message
     */
    fun e(message: String?) {
        if (message == null)
            return
        if (DOLOG) {
            Log.e(TAG, message)
        }
    }

    /**
     * Error Log
     * @param message
     */
    fun e(tag: String, message: String?) {
        if (message == null)
            return
        if (DOLOG) {
            Log.e(tag, message)
        }

    }

    fun printStackTrace(e: Exception) {

        if (DOLOG) {
            e.printStackTrace()
        }

    }

    fun e(tag: String, message: String?, throwable: Throwable) {
        if (message == null)
            return
        if (DOLOG) {
            Log.e(tag, message, throwable)
        }

    }

    /**
     * Debug Log
     * @param message
     */
    fun d(message: String?) {
        if (message == null)
            return
        if (DOLOG) {
            Log.d(TAG, message)
        }

    }

    /***
     *
     * @param tag
     * @param message
     */
    fun d(tag: String, message: String?) {
        if (message == null)
            return
        if (DOLOG) {
            Log.d(tag, message)
        }

    }

    fun i(tag: String, message: String?) {
        if (message == null)
            return
        if (DOLOG) {
            Log.i(tag, message)
        }

    }
}
