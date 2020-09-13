package digital.lamp.mindlamp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.activity.MainWearActivity
import digital.lamp.mindlamp.appstate.AppState
import kotlinx.android.synthetic.main.activity_pre_login_wear.*
import org.json.JSONException


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class PreLoginActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_login_wear)

        btnlogin.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                var mainIntent = Intent(this@PreLoginActivity, WearLoginActivity::class.java)
                startActivity(mainIntent)
                finish()

            }
        })

    }
}