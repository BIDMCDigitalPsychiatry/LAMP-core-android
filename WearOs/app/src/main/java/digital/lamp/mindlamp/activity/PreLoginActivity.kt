package digital.lamp.mindlamp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import digital.lamp.mindlamp.databinding.ActivityPreLoginWearBinding


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class PreLoginActivity : FragmentActivity() {

    private lateinit var binding: ActivityPreLoginWearBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreLoginWearBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnlogin.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                var mainIntent = Intent(this@PreLoginActivity, WearLoginActivity::class.java)
                startActivity(mainIntent)
                finish()

            }
        })

    }
}