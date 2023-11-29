package digital.lamp.mindlamp.utils

import digital.lamp.mindlamp.app.App
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ZCo Developer
 * This class responsible for create a file and write important logs into it.
 */
object DebugLogs {
    private val MAX_FILE_SIZE: Long=1024*1024
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
            val sd = App.app.getExternalFilesDir(null)!!
            var logfile = File(sd, "LampLog.txt")

            if (!logfile.exists()) {
                logfile.createNewFile()
            }
            else if (logfile.length()>MAX_FILE_SIZE) {
                val logfileBackup = File(sd, "LampLog_backup.txt")

                if (logfileBackup.exists()) {
                    logfileBackup.delete()
                }
                logfile.renameTo(logfileBackup)

                logfile = File(sd, "LampLog.txt")
                logfile.createNewFile()
            }

            if (sd.canWrite()) {
                val fw = FileWriter(sd.absolutePath + "/LampLog.txt", true) // the
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
    fun writeToFileTime(txt:String,milliSec:Long){
       val result = Date(milliSec)
       writeToFile("$txt::$milliSec::"+simple.format(result))
    }

}
