package digital.lamp.mindlamp.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    @SuppressLint("NewApi")
    fun toBase64(message: String): String? {
        val data: ByteArray
        try {
            data = message.toByteArray(charset("UTF-8"))
            return Base64.encodeToString(data, Base64.NO_WRAP)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    @Suppress("DEPRECATION")
    fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }

    fun getMyIntValue(vararg any: Any) : Int {
        return when(val tmp = any.first()) {
            is Number -> tmp.toInt()
            else -> throw Exception("not a number") // or do something else reasonable for your case
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getMilliFromDate(dateString: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try {
            val mDate = sdf.parse(dateString)!!
            return mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getTimestampForAnalytics(): String? {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return simpleDateFormat.format(date)
    }

}