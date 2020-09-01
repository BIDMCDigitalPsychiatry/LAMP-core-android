package digital.lamp.mindlamp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import digital.lamp.mindlamp.utils.AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
object PermissionCheck {

    fun checkAndRequestReadWritePermission(
        context: Activity
    ): Boolean {

        var retval: Boolean = false
        val readStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readcontacts =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)

        if (readStorage == PackageManager.PERMISSION_GRANTED
            && writeStorage == PackageManager.PERMISSION_GRANTED
            && readcontacts == PackageManager.PERMISSION_GRANTED
        ) {
            retval = true
        } else
            retval = false

        return retval
    }

    fun requestPermissionScreen(context: Activity) {

        val readStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readcontacts =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)

        val listPermissionsNeeded = ArrayList<String>()

        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (readcontacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }

        ActivityCompat.requestPermissions(
            context,
            listPermissionsNeeded.toTypedArray(),
            REQUEST_ID_MULTIPLE_PERMISSIONS
        )
    }
}