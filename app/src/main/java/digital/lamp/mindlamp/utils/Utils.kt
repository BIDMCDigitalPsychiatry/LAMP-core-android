package digital.lamp.mindlamp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.R
import java.io.File
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
        sdf.timeZone =TimeZone.getDefault()

        try {
            val mDate = sdf.parse(dateString)!!
            val formattedDate: String = sdf.format(mDate)
            val localTime = sdf.parse(formattedDate)!!
            return localTime.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getLocationAuthorizationStatus(context: Context):String{
        var status =""
        val backgroundLocationPermissionApproved =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        val fineLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
        if(backgroundLocationPermissionApproved == PackageManager.PERMISSION_GRANTED)
            status = context.getString(R.string.location_status_background_allowed)
        else if(fineLocationPermissionApproved == PackageManager.PERMISSION_GRANTED)
            status = context.getString(R.string.location_status_allowed)
        return status
    }

    fun isDeviceIsInPowerSaveMode(context: Context):Boolean {
        val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }


    fun getAvailableInternalMemorySize(context: Context): String? {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return formatSize(context,availableBlocks * blockSize)
    }

    fun getTotalInternalMemorySize(context: Context): String? {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return formatSize(context,totalBlocks * blockSize)
    }

    private fun formatSize(context: Context,size: Long): String? {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = context.getString(R.string.kb)
            size /= 1024
            if (size >= 1024) {
                suffix = context.getString(R.string.mb)
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(java.lang.Long.toString(size))
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    fun getUserAgent():String{
        return "NativeCore " + BuildConfig.VERSION_NAME + "; Android " +Build.VERSION.RELEASE+ "; "+ Build.MANUFACTURER + "; " + Build.MODEL
    }

    fun isGPSEnabled(context: Context): Boolean {
        var enabled = false
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            ?: return enabled
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (gpsEnabled) {
            enabled = true
        }
        return enabled
    }

    fun getHttpErrorMessage(errorCode:Int,context: Context): String{
        when (errorCode) {
            400 -> {
                return context.getString(R.string.invalid_request_please_try_again)
            }
            401 -> {
                return context.getString(R.string.you_are_not_authorized)

            }
            403 -> {
                return context.getString(R.string.you_dont_have_access)
            }
            404 -> {
                return context.getString(R.string.user_not_found_here)
            }
            408 -> {
                return context.getString(R.string.request_timeout)
            }
            500 -> {
                return context.getString(R.string.something_went_wrong_on_server)
            }
            502 -> {
                return context.getString(R.string.bad_gateway)
            }
            503 -> {
                return context.getString(R.string.service_not_available)
            }
            else -> return ""
        }
    }
}