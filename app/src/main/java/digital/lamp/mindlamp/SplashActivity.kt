package digital.lamp.mindlamp

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startNodeServer()
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if (moveToHome) {
                val mainIntent = Intent(this, HomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, 3000)


//        val bundle = intent.extras
//        if (bundle != null) {
//            try {
//                if (bundle.containsKey("page")) {
//                    val path = bundle.getString("page")
//                    if(path != null && path.isNotEmpty()){
//                        moveToHome = false
//                        val mainIntent = Intent(this, CustomWebviewActivity::class.java)
//                        mainIntent.putExtra("survey_path",path)
//                        startActivity(mainIntent)
//                        finish()
//                    }
//                    Log.e("Splash : "," : $path")
//                }
//            } catch (ex: JSONException) {
//                ex.printStackTrace()
//            }
//        }

    }

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

}