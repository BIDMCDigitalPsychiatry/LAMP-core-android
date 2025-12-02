package digital.lamp.mindlamp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import digital.lamp.lamp_kotlin.lamp_core.apis.RenewAccessTokenAPI
import digital.lamp.lamp_kotlin.lamp_core.infrastructure.ClientException
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.model.TokenResponseData
import java.io.File
import java.io.UnsupportedEncodingException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * This class responsible for common util methods.
 */
object Utils {
    /**
     * This method used for @param  convert to base64
     */
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

    /**
     * To check the @param service running or not.
     */
    @Suppress("DEPRECATION")
    fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }

    /**
     * Convert any number into integer
     */
    fun getMyIntValue(vararg any: Any): Int {
        return when (val tmp = any.first()) {
            is Number -> tmp.toInt()
            else -> throw Exception("not a number") // or do something else reasonable for your case
        }
    }

    /**
     * Convert date to milliseconds
     */
    @SuppressLint("SimpleDateFormat")
    fun getMilliFromDate(dateString: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getDefault()

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

    /**
     * To fetch location authorization status
     */
    fun getLocationAuthorizationStatus(context: Context): String {
        var status = ""
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
        if (backgroundLocationPermissionApproved == PackageManager.PERMISSION_GRANTED)
            status = context.getString(R.string.location_status_background_allowed)
        else if (fineLocationPermissionApproved == PackageManager.PERMISSION_GRANTED)
            status = context.getString(R.string.location_status_allowed)
        return status
    }

    /**
     * To check the device is in power saving mode
     */
    fun isDeviceIsInPowerSaveMode(context: Context): Boolean {
        val powerManager: PowerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }


    /**
     * To get the available internal memory size
     */
    fun getAvailableInternalMemorySize(context: Context): String? {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return formatSize(context, availableBlocks * blockSize)
    }

    /**
     * To fetch device total internal memory size
     */
    fun getTotalInternalMemorySize(context: Context): String? {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.getPath())
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return formatSize(context, totalBlocks * blockSize)
    }

    /**
     * Formats a file size into a human-readable string.
     *
     * @param context The application context for accessing resources.
     * @param size The file size in bytes.
     * @return A formatted string representing the file size.
     */
    private fun formatSize(context: Context, size: Long): String? {
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
    /**
     * Constructs a user-agent string for the application.
     *
     * The user-agent string includes information about the application version,
     * Android version, device manufacturer, and device model.
     *
     * @return A formatted user-agent string.
     */
    fun getUserAgent(): String {
        return "NativeCore " + BuildConfig.VERSION_NAME + "; Android " + Build.VERSION.RELEASE + "; " + Build.MANUFACTURER + "; " + Build.MODEL
    }
    /**
     * Checks if the GPS (Global Positioning System) is enabled on the device.
     *
     * @param context The application context for accessing system services.
     * @return `true` if GPS is enabled, `false` otherwise.
     */
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
    /**
     * Get a user-friendly error message based on the provided HTTP error code.
     *
     * @param errorCode The HTTP error code.
     * @param context The application context for accessing string resources.
     * @return A user-friendly error message corresponding to the given HTTP error code.
     */
    fun getHttpErrorMessage(errorCode: Int, context: Context): String {
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
                return context.getString(R.string.something_went_wrong)
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
    fun serverUsesAccessToken(): Boolean {
        return AppState.session.accessToken.isNotEmpty()
    }

    suspend fun <T> apiWithRetry(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: ClientException) {
            LampLog.printStackTrace(e)

            // If server does NOT use access token → no retry
            if (!serverUsesAccessToken()) {
                throw e
            }

            // If server uses token but error is NOT 401 → no retry
            if (e.statusCode != 401) {
                throw e
            }

            // Refresh token
            val refreshed = refreshToken()
            if (!refreshed) throw e

            // Retry the original API call with the new token
            apiCall()
        }
    }

    suspend fun refreshToken(): Boolean {
        return try {
            val authHeader = "Bearer ${AppState.session.refreshtoken}"

            val body = mapOf(
                "refreshToken" to AppState.session.refreshtoken
            )

            val response = RenewAccessTokenAPI(AppState.session.serverAddress)
                .renewAccessToken(authHeader, body)

            val resObj = Gson().fromJson(response.toString(), TokenResponseData::class.java)
            Log.e("refreshToken", "response: $response")

            AppState.session.refreshtoken = resObj.data.refresh_token
            AppState.session.accessToken = resObj.data.access_token

            true
        } catch (ex: Exception) {
            LampLog.printStackTrace(ex)
            false
        }
    }
}