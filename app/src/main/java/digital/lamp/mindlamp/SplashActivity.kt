package digital.lamp.mindlamp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import digital.lamp.mindlamp.databinding.ActivityHomeBinding
import digital.lamp.mindlamp.databinding.ActivitySplashBinding
import org.json.JSONException


/**
 * Created by ZCO Engineering Dept. on 14,February,2020
 */
class SplashActivity : AppCompatActivity() {


    private var moveToHome : Boolean =  true
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if(moveToHome) {
                val mainIntent = Intent(this, HomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, 3000)

    }

}