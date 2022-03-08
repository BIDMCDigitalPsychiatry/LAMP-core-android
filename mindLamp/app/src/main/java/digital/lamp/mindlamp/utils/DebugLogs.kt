package digital.lamp.mindlamp.utils

import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.app.App
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ZCo Developer
 */
object DebugLogs {
    /**
     * Saving Debugg Logs
     *
     * @param txt
     */
    fun writeToFile(txt: String?) {
//        if (BuildConfig.DO_LOG) {
            try {
                val  sd = App.app.getExternalFilesDir(null)!!
                val logfile = File(sd, "LampLog.txt")

                if (!logfile.exists()) {
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

//        }
    }


}
