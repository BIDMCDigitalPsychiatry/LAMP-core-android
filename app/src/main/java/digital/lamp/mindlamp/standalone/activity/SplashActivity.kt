package digital.lamp.mindlamp.standalone.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import digital.lamp.mindlamp.standalone.R
import digital.lamp.mindlamp.standalone.appstate.AppState
import org.json.JSONException


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class SplashActivity : WearableActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            var mainIntent = Intent()
            if (!AppState.session.isLoggedIn) {
                /* Create an Intent that will start the WEarlogin-Activity. */
                mainIntent = Intent(this, WearLoginActivity::class.java)

            } else {
                mainIntent = Intent(this, MainWearActivity::class.java)
            }

            startActivity(mainIntent)
            finish()
        }, 3000)

    }


}