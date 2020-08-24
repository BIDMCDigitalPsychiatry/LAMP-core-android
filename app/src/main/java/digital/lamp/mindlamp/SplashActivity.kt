package digital.lamp.mindlamp

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.io.File
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.*

import java.io.*
import android.content.SharedPreferences

import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.aware.LogUtils
import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.network.model.UserAgent
import digital.lamp.mindlamp.repository.HomeRepository
import digital.lamp.mindlamp.utils.*
import digital.lamp.mindlamp.utils.LampLog
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class SplashActivity : AppCompatActivity() {

    companion object {

        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("node")
        }

        private var _startedNodeAlready = false

    }

    private var moveToHome: Boolean = true
    var msg: String? = ""
    var lastMsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        AppState.iv = RandomString.getStringVal(16)!!
        AppState.encrypteddata =
            Utils.AESEncrypt(AppState.deviceid!!, AppState.key!!, AppState.iv!!).toString()
        invokeGetVersionUrl()

//        startNodeServer()
//        }

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if (moveToHome) {
                val mainIntent = Intent(this, HomeActivity::class.java)
//                startActivity(mainIntent)
//                finish()
            }
        }, 3000)

        displayProgress(true, "Lasfdsdf")


//        val decryptedstring: String? = Utils.AESDecrypt(AESCipher1, key1, iv1)
//        Log.d("ENCRYPT", decryptedstring)
//        downloadImage("https://johncodeos.com/images/funnycat.png")

    }

    /*  @SuppressWarnings
      private fun downloadNode(context: Context, url: String) {

          val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

          val directory = File(Environment.DIRECTORY_DOWNLOADS)

          if (!directory.exists()) {
              directory.mkdirs()
          }

          val downloadUri = Uri.parse(url)

          val request = DownloadManager.Request(downloadUri).apply {
              setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                  .setAllowedOverRoaming(false)
                  .setTitle(url.substring(url.lastIndexOf("/") + 1))
                  .setDescription("")
                  .setDestinationInExternalPublicDir(
                      directory.toString(),
                      url.substring(url.lastIndexOf("/") + 1)
                  )
          }

          val downloadId = downloadManager.enqueue(request)
          val query = DownloadManager.Query().setFilterById(downloadId)
          Thread(Runnable {
              var downloading = true
              while (downloading) {
                  val cursor: Cursor = downloadManager.query(query)
                  cursor.moveToFirst()
                  if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                      downloading = false
                      var zipHelper: ZipHelper = ZipHelper()
                      val directory = getExternalFilesDir(Context.MODE_PRIVATE.toString())
                      zipHelper.unzip(
                          directory?.absolutePath + "/" + "sample-large-zip-file.zip",
                          Environment.DIRECTORY_DOWNLOADS
                      )
                  }
                  val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                  *//* val msg = statusMessage(url, directory, status)
                 if (msg != lastMsg) {
                     this@SplashActivity.runOnUiThread {
                         Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                     }
                     lastMsg = msg ?: ""
                 }*//*
                cursor.close()
            }
        }).start()
    }*/

    private fun startNodeServer() {

        if (!_startedNodeAlready) {
            _startedNodeAlready = true

            /////////////////////////////////////

            Thread(Runnable {
                //The path where we expect the node project to be at runtime.
                var nodeDir =
                    getApplicationContext().getFilesDir().getAbsolutePath() + "/lampBuild";

                /* var nodeDir =
                     getApplicationContext().getFilesDir().getAbsolutePath() + "/nodejs-project";*/

                Log.d("NODE", nodeDir)
                if (wasAPKUpdated()) {
                    //Recursively delete any existing nodejs-project.
                    var nodeDirReference = File(nodeDir);
                    if (nodeDirReference.exists()) {
                        deleteFolderRecursively(File(nodeDir));
                    }

                    Log.d("NODE", "Assets " + getApplicationContext().getAssets())
                    //Copy the node project from assets into the application's data path.
                    copyAssetFolder(getApplicationContext().getAssets(), "lampBuild", nodeDir);
//                    copyAssetFolder(getApplicationContext().getAssets(), "nodejs-project", nodeDir);

                    saveLastUpdateTime();
                }

                ///for directly calling javascript from code and execute
                /*startNodeWithArguments(
                    arrayOf(
                        "node", "-e",
                        "var http = require('http'); " +
                                "var versions_server = http.createServer( (request, response) => { " +
                                "  response.end('Versions: ' + JSON.stringify(process.versions)); " +
                                "}); " +
                                "versions_server.listen(3001);")
                )*/
                /* startNodeWithArguments(
                     arrayOf(
                         "node",
                         nodeDir + "/main.js"
                     )
                 );*/

                startNodeWithArguments(
                    arrayOf(
                        "node",
                        nodeDir + "/index.js"
                    )
                );
            }).start();


            ////////////////////////////////////////////
///for directly calling javascript from code and execute
            //Network operations should be done in the background.
            /* object : AsyncTask<Void?, Void?, String?>() {

             override fun onPostExecute(result: String?) {
 //                textViewVersions.setText(result)
             }

             override fun doInBackground(vararg p0: Void?): String? {
                 var nodeResponse = ""
                 try {

                     Log.d("NODE", "executing server")
                     val localNodeServer = URL("http://localhost:3001/")
                     val `in` = BufferedReader(
                         InputStreamReader(localNodeServer.openStream())
                     )
                     var inputLine: String
                     while (`in`.readLine().also { inputLine = it } != null) nodeResponse =
                         nodeResponse + inputLine
                     `in`.close()
                 } catch (ex: java.lang.Exception) {
                     nodeResponse = ex.toString()
                 }
                 return nodeResponse
             }
         }.execute()*/
        }
    }

    private fun deleteFolderRecursively(file: File): Boolean {
        return try {
            var res = true
            for (childFile in file.listFiles()) {
                res = if (childFile.isDirectory()) {
                    res and deleteFolderRecursively(childFile)
                } else {
                    res and childFile.delete()
                }
            }
            res = res and file.delete()
            res
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun copyAssetFolder(
        asset: AssetManager,
        fromAssetPath: String,
        toPath: String
    ): Boolean {
        try {
            var files = asset.list(fromAssetPath);
            var res = true;

            if (files!!.size == 0) {
                //If it's a file, it won't have any assets "inside" it.
                res = copyAsset(
                    asset,
                    fromAssetPath,
                    toPath
                );
            } else {
                File(toPath).mkdirs();
                for (file in files.orEmpty())
                    res = copyAssetFolder(
                        asset,
                        fromAssetPath + "/" + file,
                        toPath + "/" + file
                    );
            }
            return res;
        } catch (e: Exception) {
            e.printStackTrace();
            return false;
        }
    }

    private fun copyAsset(
        assetManager: AssetManager,
        fromAssetPath: kotlin.String,
        toPath: kotlin.String
    ): kotlin.Boolean {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        return try {
            `in` = assetManager.open(fromAssetPath)
            File(toPath).createNewFile()
            out = FileOutputStream(toPath)
            copyFile(`in`, out)
            `in`.close()
            `in` = null
            out.flush()
            out.close()
            out = null
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    fun copyFile(`in`: InputStream?, out: OutputStream?): kotlin.Unit {
        var buffer: kotlin.ByteArray? = kotlin.ByteArray(1024)
        var read = 0

        while (`in`!!.read(buffer).also { read = it } != -1) {
            out!!.write(buffer, 0, read)
        }

    }

    fun wasAPKUpdated(): kotlin.Boolean {
        val prefs = applicationContext.getSharedPreferences(
            "NODEJS_MOBILE_PREFS",
            MODE_PRIVATE
        )
        val previousLastUpdateTime =
            prefs.getLong("NODEJS_MOBILE_APK_LastUpdateTime", 0)
        var lastUpdateTime: kotlin.Long = 1
        try {
            val packageInfo = applicationContext.packageManager
                .getPackageInfo(applicationContext.packageName, 0)
            lastUpdateTime = packageInfo.lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return lastUpdateTime != previousLastUpdateTime
    }

    private fun saveLastUpdateTime(): kotlin.Unit {
        var lastUpdateTime: kotlin.Long = 1
        try {
            val packageInfo = applicationContext.packageManager
                .getPackageInfo(applicationContext.packageName, 0)
            lastUpdateTime = packageInfo.lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val prefs = applicationContext.getSharedPreferences(
            "NODEJS_MOBILE_PREFS",
            MODE_PRIVATE
        )
        val editor = prefs.edit()
        editor.putLong("NODEJS_MOBILE_APK_LastUpdateTime", lastUpdateTime)
        editor.commit()
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun startNodeWithArguments(arguments: Array<String?>?): Int?

    fun loadHomeScreenDelayed() {

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if (moveToHome) {
                val mainIntent = Intent(this, HomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, 25000)

    }

    fun displayProgress(state: Boolean, txt: String) {

        var statusprogress = progressbar;
        var txtpg = pgtext

        if (null != statusprogress) {
            if (state) {
                statusprogress.visibility = View.VISIBLE;
                txtpg?.visibility = View.VISIBLE;
                txtpg?.text = txt;
            } else {
                statusprogress.visibility = View.GONE;
                txtpg?.visibility = View.GONE;
            }


        }


    }

    fun invokeGetVersionUrl() {
        val homeRepository = HomeRepository()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                var response = homeRepository.getVersionUrl()
                LampLog.e("RESPONSE", " : $response")

                when (response.code()) {
                    400 -> {
                        val logEventRequest = LogEventRequest(
                            "Network error - 400 Bad Request",
                            UserAgent(),
                            AppState.session.userId
                        )
                        LogUtils.invokeLogData(
                            Utils.getApplicationName(this@SplashActivity),
                            "error",
                            logEventRequest
                        )
                    }
                    401 -> {
                        val logEventRequest = LogEventRequest(
                            "Network error - 401 Unauthorized",
                            UserAgent(),
                            AppState.session.userId
                        )
                        LogUtils.invokeLogData(
                            Utils.getApplicationName(this@SplashActivity),
                            "error",
                            logEventRequest
                        )
                    }
                    403 -> {
                        val logEventRequest = LogEventRequest(
                            "Network error - 403 Forbidden",
                            UserAgent(),
                            AppState.session.userId
                        )
                        LogUtils.invokeLogData(
                            Utils.getApplicationName(this@SplashActivity),
                            "error",
                            logEventRequest
                        )
                    }
                    404 -> {
                        val logEventRequest = LogEventRequest(
                            "Network error - 404 Not Found",
                            UserAgent(),
                            AppState.session.userId
                        )
                        LogUtils.invokeLogData(
                            Utils.getApplicationName(this@SplashActivity),
                            "error",
                            logEventRequest
                        )
                    }
                    500 -> {
                        val logEventRequest = LogEventRequest(
                            "Network error - 500 Internal Server Error",
                            UserAgent(),
                            AppState.session.userId
                        )
                        LogUtils.invokeLogData(
                            Utils.getApplicationName(this@SplashActivity),
                            "error",
                            logEventRequest
                        )
                    }

                }


                var strresponse = response.body()!!.string()
                //creating json object
                val jsoncontact: JSONObject = JSONObject(strresponse)
                val url: String = jsoncontact.getString("url")
                val latestversion: String = jsoncontact.getString("version")
                val currentversion: String = AppState.session.currentversion
                LampLog.e("RESPONSE", " : $url")
                LampLog.e("VERSION", "latest version : $latestversion")
                LampLog.e("VERSION", "current version : $currentversion")

                if (latestversion.equals(currentversion)) {
                    loadHomeScreenDelayed()

                } else {

                    if (PermissionCheck.checkAndRequestReadWritePermission(this@SplashActivity)) {
                        downloadNodeFromServer("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-large-zip-file.zip")
                    }

                }


            } catch (er: Exception) {
                er.printStackTrace()
            }
        }
    }

    private fun downloadNodeFromServer(url: String) {
        /* val directory = cacheDir

         if (!directory.exists()) {
             directory.mkdirs()
         }*/

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)
//        val uri = Uri.parse(File(directory.toString() + url.substring(url.lastIndexOf("/") + 1)))

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalFilesDir(
                    this@SplashActivity,
                    Context.MODE_PRIVATE.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false

                    var zipHelper: ZipHelper = ZipHelper()

                    val directory = getExternalFilesDir(Context.MODE_PRIVATE.toString())
                    val toPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/lampBuild"
                    if(zipHelper.unzip(directory?.absolutePath + "/" + url.substring(url.lastIndexOf("/") + 1),toPath)!!){
                        startNodeServer()
                    }
                    Log.d("ENCRYPT", AppState.encrypteddata)
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }

    private fun statusMessage(url: String, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully"

            else -> "There's nothing to download"
        }
        return msg
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {

                        downloadNodeFromServer("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-large-zip-file.zip")


                        //else any one or both the permissions are not granted
                    } else {
                        //Now further we check if used denied permanently or not
                        // case 4 User has denied permission but not permanently
                        showDialogOK(getString(R.string.permission_error),
                            DialogInterface.OnClickListener { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> PermissionCheck.checkAndRequestPermissions(
                                        this
                                    )
                                    DialogInterface.BUTTON_NEGATIVE ->
                                        // proceed with logic by disabling the related features or quit the app.
                                        finish()
                                }
                            })
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), okListener)
            .create()
            .show()
    }
}