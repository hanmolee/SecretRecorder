package hanmo.com.secretrecoder

import android.animation.Animator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import kotlinx.android.synthetic.main.splashscreen.*


/**
 * Created by hanmo on 2018. 5. 14..
 */
class SplashActivity : AppCompatActivity() {

    /** 처음 액티비티가 생성될때 불려진다.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        with(splashLottie) {
            playAnimation()
            speed = 3f
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
        }
    }

}