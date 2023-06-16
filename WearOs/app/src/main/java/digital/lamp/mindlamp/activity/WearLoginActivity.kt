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
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.databinding.ActivityLoginWearBinding
import digital.lamp.mindlamp.sensor.health_services.SensorStore
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.NetworkUtils
import digital.lamp.mindlamp.utils.Utils
import digital.lamp.mindlamp.viewmodels.DataViewModel
import digital.lamp.mindlamp.web.WebConstant
import digital.lamp.mindlamp.web.WebServiceResponseData
import digital.lamp.mindlamp.web.pojo.response.UserExistResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class WearLoginActivity : FragmentActivity() {


    private var moveToHome: Boolean = true
    private var dataViewModel: DataViewModel? = null
    private var toast: Toast? = null;
    private var mLifecycleRegistry: LifecycleRegistry? = null
    private lateinit var binding: ActivityLoginWearBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWearBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mLifecycleRegistry = LifecycleRegistry(this);
        mLifecycleRegistry!!.markState(Lifecycle.State.CREATED);
        dataViewModel = ViewModelProvider(this@WearLoginActivity).get(DataViewModel::class.java)

        binding.imgicon.setOnClickListener {
            var mainIntent = Intent(this@WearLoginActivity, SeverUrlActivity::class.java)
            startActivity(mainIntent)
        }


        binding.btndone.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                Utils.displayProgress(binding.progressbar, binding.pgtext, true, "")
                if (NetworkUtils.isNetworkAvailable(this@WearLoginActivity)) {

                    WebConstant.USERID =
                        Utils.toBase64(
                           // "U8917110983@lamp.com" + ":" + "U8917110983"
                            binding.txtusername.text.toString().trim() + ":" + binding.txtpwd.text.toString()

                                .trim()
                        ).toString().trim()

                    dataViewModel!!.isUserExists("me")


                } else {

                    Utils.displayProgress(binding.progressbar, binding.pgtext, false, "")
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
                    Utils.displayProgress(binding.progressbar, binding.pgtext, false, "")

                    var msg: String = ""
                    when (t?.responseCode) {

                        WebConstant.CODE_SUCCESS -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                try {
                                    synchronized(SensorStore) {
                                        SensorStore.clear()
                                    }
                                } catch (e: Exception) {
                                    LampLog.d("Exception", "clear   sensor values")
                                }
                            }

                            AppState.session.userId = Utils.toBase64(
                                //"U8917110983@lamp.com" + ":" + "U8917110983"
                                binding.txtusername.text.toString().trim() + ":" + binding.txtpwd.text.toString()

                                    .trim()
                            ).toString().trim()
                            AppState.session.username =
                                ((t?.responseBase as UserExistResponse).lstuserdetails.get(0).id.trim())

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