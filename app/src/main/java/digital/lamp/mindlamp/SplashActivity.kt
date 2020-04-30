package digital.lamp.mindlamp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class SplashActivity : AppCompatActivity() {


    private var moveToHome : Boolean =  true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if(moveToHome) {
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


}