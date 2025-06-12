package digital.lamp.mindlamp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import digital.lamp.mindlamp.databinding.ActivitySplashBinding

/**
 * SplashScreen that appears when the app is launched.
 * This activity is responsible for initializing essential resources
 * and navigating to the appropriate screen after a brief delay.
 */
class SplashActivity : BaseActivity() {


    private var moveToHome: Boolean = true
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if (moveToHome) {
                val mainIntent = Intent(this, HomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, 3000)

    }

}