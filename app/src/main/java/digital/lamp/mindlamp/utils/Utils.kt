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
        sdf.timeZone =TimeZone.getDefault()// TimeZone.getTimeZone("UTC")

        try {
            val mDate = sdf.parse(dateString)!!
           // sdf.timeZone = TimeZone.getDefault()
            val formattedDate: String = sdf.format(mDate)
            val localTime = sdf.parse(formattedDate)!!
            return localTime.time
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

    @Suppress("DEPRECATION")
    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->    true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->   true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->   true
                else ->     false
            }
        }
        // For below 29 api
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    fun permissionStatus(context: Context){
       /* val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val requestedPermissions = info.requestedPermissions //This array contains the requested permissions.
        val permissions = info.permissions
        Log.e("PermissionStatus ", "permissions " + permissions.size)*/

        val granted: MutableList<String> = ArrayList()
        try {
            val pi: PackageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            for (i in pi.requestedPermissions.indices) {
                if ((pi.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) == PackageInfo.REQUESTED_PERMISSION_GRANTED){
                    granted.add(pi.requestedPermissions[i])
                }
            }
        } catch (e: java.lang.Exception) {
        }
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


    fun getAvailableInternalMemorySize(): String? {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return formatSize(availableBlocks * blockSize)
    }

    fun getTotalInternalMemorySize(): String? {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return formatSize(totalBlocks * blockSize)
    }



    private fun formatSize(size: Long): String? {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = "KB"
            size /= 1024
            if (size >= 1024) {
                suffix = "MB"
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
    fun isLocationEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager?.let { LocationManagerCompat.isLocationEnabled(it) } ?: false
    }
}