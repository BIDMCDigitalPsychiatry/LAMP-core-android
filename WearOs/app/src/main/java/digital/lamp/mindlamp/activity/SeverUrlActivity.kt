package digital.lamp.mindlamp.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.databinding.ActivityServerurlBinding


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class SeverUrlActivity : FragmentActivity() {

    private lateinit var binding: ActivityServerurlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServerurlBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.txtserverurl.setText(AppState.session.urlvalue)

        binding.imgservericon.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                //if user has edited or is viewing it then save it
                AppState.session.urlvalue = binding.txtserverurl.text.toString().trim()
                finish()
            }
        })


    }

}