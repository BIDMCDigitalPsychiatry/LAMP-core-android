package digital.lamp.mindlamp.utils

import digital.lamp.mindlamp.app.App
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by ZCo Developer
 */
object DebugLogs {
    val simple: DateFormat = SimpleDateFormat(
        "dd MMM yyyy HH:mm:ss:SSS Z"
    )

    /**
     * Saving Debugg Logs
     *
     * @param txt
     */
    fun writeToFile(txt: String?) {
        try {

            val logfile = File(App.app.filesDir, "LampLog.txt")

            if (!logfile.exists()) {
                logfile.createNewFile()
            }

            if (logfile.canWrite()) {
                val fw = FileWriter(logfile, true) // the
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentTime = sdf.format(Date())

                fw.write("$currentTime: $txt\n")// appends the string to the file

                fw.flush()
                fw.close()
            }
        } catch (e: Exception) {
            LampLog.e(e.toString())
        }
    }

    fun writeToFileTime(txt: String, milliSec: Long) {
        val result = Date(milliSec)
        writeToFile("$txt::$milliSec::" + simple.format(result))
    }

}
