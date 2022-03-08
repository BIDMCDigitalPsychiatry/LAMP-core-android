package digital.lamp.mindlamp.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import kotlinx.android.synthetic.main.activity_serverurl.*
import java.util.*


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class SeverUrlActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serverurl)

        txtserverurl.setText(AppState.session.urlvalue)

        imgservericon.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                //if user has edited or is viewing it then save it
                AppState.session.urlvalue = txtserverurl.text.toString().trim()
                finish()
            }
        })


    }

}