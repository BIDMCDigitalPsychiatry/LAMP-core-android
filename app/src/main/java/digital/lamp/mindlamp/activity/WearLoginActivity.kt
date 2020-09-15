package digital.lamp.mindlamp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import digital.lamp.mindlamp.BuildConfig
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import digital.lamp.mindlamp.web.WebConstant
import digital.lamp.mindlamp.web.WebServiceResponseData
import digital.lamp.mindlamp.web.pojo.response.UserExistResponse
import kotlinx.android.synthetic.main.activity_login_wear.*
import kotlinx.android.synthetic.main.activity_login_wear.pgtext
import kotlinx.android.synthetic.main.activity_login_wear.progressbar
import kotlinx.android.synthetic.main.activity_main_wear.*


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

        if (BuildConfig.DEBUG) {
            txtusername.setText("U3998365801")
            txtpwd.setText("12345")

        }
        imgicon.setOnClickListener {
            var mainIntent = Intent(this@WearLoginActivity, SeverUrlActivity::class.java)
            startActivity(mainIntent)
        }

        btndone.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                Utils.displayProgress(progressbar, pgtext, true, "")
                if (NetworkUtils.isNetworkAvailable(this@WearLoginActivity)) {

                    WebConstant.USERID =
                        Utils.toBase64(
                            txtusername.text.toString().trim() + ":" + txtpwd.text.toString()
                                .trim()
                        ).toString().trim()

                    dataViewModel!!.isUserExists(txtusername.text.toString())


                } else {

                    Utils.displayProgress(progressbar, pgtext, false, "")
                    Toast.makeText(
                        this@WearLoginActivity,
                        getString(R.string.internet_error),
                        Toast.LENGTH_LONG
                    )
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
                    Utils.displayProgress(progressbar, pgtext, false, "")

                    var msg: String = ""
                    when (t?.responseCode) {

                        WebConstant.CODE_SUCCESS -> {
                            val userId =
                                (t?.responseBase as UserExistResponse).lstuserdetails.get(0).id
                            AppState.session.username = txtusername.text.toString().trim()
                            AppState.session.userId = userId

                            val intent =
                                Intent(this@WearLoginActivity, MainWearActivity::class.java)
                            startActivity(intent)

                            finish()

                        }
                        WebConstant.CODE_INVALID_USER -> {
                            msg = getString(R.string.authentication_failed)
                            toast = Toast.makeText(
                                this@WearLoginActivity,
                                msg,
                                Toast.LENGTH_SHORT
                            )
                            toast?.show()

                        }

                        else -> {

                            msg = getString(R.string.server_error)
                            toast = Toast.makeText(
                                this@WearLoginActivity,
                                msg,
                                Toast.LENGTH_SHORT
                            )
                            toast?.show()
                        }

                    }

                }
            })

    }

}