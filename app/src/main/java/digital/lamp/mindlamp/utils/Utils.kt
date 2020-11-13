package digital.lamp.mindlamp.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.util.Base64
import java.io.UnsupportedEncodingException


object Utils {
    fun toBase64(message: String): String? {
        val data: ByteArray
        try {
            data = message.toByteArray(charset("UTF-8"))
            return Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    fun getApplicationName(context: Context): String {
        return context.applicationInfo.loadLabel(context.packageManager).toString()
    }

    @Suppress("DEPRECATION")
    fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }
}