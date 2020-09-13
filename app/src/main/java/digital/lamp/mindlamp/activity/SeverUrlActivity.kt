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

    val SPEECH_RECOGNATION_RETVAL: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serverurl)

        txtserverurl.setText(AppState.session.urlvalue)

        txtserverurl.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                promptSpeechInput(
                    this@SeverUrlActivity,
                    SPEECH_RECOGNATION_RETVAL,
                    getString(R.string.speech_prompt)
                )
            }
        })
        imgservericon.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                //if user has edited or is viewing it then save it
                AppState.session.urlvalue = txtserverurl.text.toString().trim()
                finish()
            }
        })


    }

    fun promptSpeechInput(
        activity: Activity,
        requestCode: Int,
        promtMsg: String?
    ) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promtMsg)
        try {
            activity.startActivityForResult(intent, requestCode)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this, a.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if ((requestCode == SPEECH_RECOGNATION_RETVAL) && (null != data)) {
            val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val voiceInput = result[0]
            txtserverurl.setText(voiceInput)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}