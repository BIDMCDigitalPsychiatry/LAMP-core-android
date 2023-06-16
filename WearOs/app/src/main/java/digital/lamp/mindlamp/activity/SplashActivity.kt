package digital.lamp.mindlamp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState


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
                mainIntent = Intent(this, PreLoginActivity::class.java)

            } else {
                mainIntent = Intent(this, MainWearActivity::class.java)
            }

            startActivity(mainIntent)
            finish()
        }, 3000)

    }


}