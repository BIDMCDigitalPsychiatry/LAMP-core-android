package digital.lamp.mindlamp.utils

import android.content.Context
import android.util.Base64
import android.view.View
import android.widget.TextView
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

    fun displayProgress(progressbar: View, pgtext: TextView, state: Boolean, txt: String) {

        if (null != progressbar) {
            if (state) {
                progressbar.visibility = View.VISIBLE;
                pgtext?.text = txt;
            } else {
                progressbar.visibility = View.GONE;
                pgtext?.visibility = View.GONE;
            }


        }


    }
}