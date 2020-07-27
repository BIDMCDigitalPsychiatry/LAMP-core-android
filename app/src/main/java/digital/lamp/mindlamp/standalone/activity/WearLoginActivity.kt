package digital.lamp.mindlamp.standalone.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import digital.lamp.mindlamp.standalone.BuildConfig
import digital.lamp.mindlamp.standalone.R
import digital.lamp.mindlamp.standalone.appstate.AppState
import digital.lamp.mindlamp.standalone.utils.NetworkUtils
import digital.lamp.mindlamp.standalone.utils.Utils
import digital.lamp.mindlamp.standalone.viewmodels.DataViewModel
import digital.lamp.mindlamp.standalone.web.WebConstant
import digital.lamp.mindlamp.standalone.web.WebServiceResponseData
import kotlinx.android.synthetic.main.activity_login_wear.*


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class WearLoginActivity : FragmentActivity() {


    private var moveToHome: Boolean = true
    private var dataViewModel: DataViewModel? = null
    private var toast: Toast? = null;
    private var mLifecycleRegistry: LifecycleRegistry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_wear)

        mLifecycleRegistry = LifecycleRegistry(this);
        mLifecycleRegistry!!.markState(Lifecycle.State.CREATED);
//        dataViewModel = ViewModelProviders.of(this@WearLoginActivity).get(DataViewModel::class.java)
        dataViewModel = ViewModelProvider(this@WearLoginActivity).get(DataViewModel::class.java)

        btndone.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                AppState.session.clearData()
                if (NetworkUtils.isNetworkAvailable(this@WearLoginActivity)) {
                    WebConstant.UN = txtusername.text.toString().trim()
                    WebConstant.PWD = txtpwd.text.toString().trim()
                    AppState.session.userId =
                        Utils.toBase64(WebConstant.UN + ":" + WebConstant.PWD).toString().trim()

                    dataViewModel!!.isUserExists(txtusername.text.toString())
                } else {
                    Toast.makeText(this@WearLoginActivity, getString(R.string.internet_error), Toast.LENGTH_LONG)
                        .show()
                }

            }
        })

        setObserver()
    }

    fun setObserver() {
        dataViewModel?.webServiceResponseLiveData?.observe(
            this@WearLoginActivity,
            object : Observer<WebServiceResponseData> {
                override fun onChanged(t: WebServiceResponseData?) {

                    Log.d("LOGIN ACTIVITY", "on Changed")
                    if (null != t?.responseBase) {
//                        var userExistResponse = t?.responseCode as UserExistResponse

                        if (t?.responseCode == WebConstant.CODE_SUCCESS) {

                            WebConstant.USERID =
                                Utils.toBase64(WebConstant.UN + ":" + WebConstant.PWD).toString()
                                    .trim()

                            val intent =
                                Intent(this@WearLoginActivity, MainWearActivity::class.java)
                            startActivity(intent)

                            finish()

                        } else {
                            toast = Toast.makeText(
                                this@WearLoginActivity,
                                t?.responseMessage,
                                Toast.LENGTH_SHORT
                            )
                            toast?.show()
                        }
                    } else {

                        toast = Toast.makeText(
                            this@WearLoginActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT
                        )
                        toast?.show()
                    }
                }
            })

    }

}