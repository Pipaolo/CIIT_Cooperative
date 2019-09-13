package edu.ciit.cooperative

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import kotlinx.android.synthetic.main.activity_intro_page.*


class IntroPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_page)


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        intro_iv_logo.load(R.drawable.logo){
            size(400,400)
        }

        val animate = AnimatorSet()
        val tX = ObjectAnimator.ofFloat(intro_iv_logo, View.TRANSLATION_X, 0f, displayMetrics.widthPixels * (-0.60).toFloat())
        tX.setDuration(600)
        tX.interpolator = AccelerateDecelerateInterpolator()

        val fade = ObjectAnimator.ofFloat(intro_tv_title, View.ALPHA, 0f, 1.0f)
        animate.playTogether(tX, fade)
        animate.setDuration(1000)

        val animate2 = AnimatorSet()

        val tY = ObjectAnimator.ofFloat(intro_tv_title, View.TRANSLATION_Y, 0f, 125f)
        tY.setDuration(1000)
        tY.interpolator = AccelerateDecelerateInterpolator()

        animate2.play(animate).before(tY)
        animate2.start()

        animate2.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                Handler().postDelayed(Runnable {
                    startActivity(Intent(this@IntroPage, LoginPage::class.java))
                    finish()
                }, 2000)
            }
        })
    }
}
