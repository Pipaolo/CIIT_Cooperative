package edu.ciit.cooperative

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class IntroPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_page)


        val imageView = findViewById(R.id.intro_iv_logo) as ImageView
        val textView: TextView = findViewById(R.id.intro_tv_title)

        Picasso.get().load(R.drawable.logo).resize(400, 400).into(imageView)

        val animate = AnimatorSet()
        val tX = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, 0f, -650f)
        tX.setDuration(600)
        tX.interpolator = AccelerateDecelerateInterpolator()

        val fade = ObjectAnimator.ofFloat(textView, View.ALPHA, 0f, 1.0f)
        animate.playTogether(tX, fade)
        animate.setDuration(1000)

        val animate2 = AnimatorSet()

        val tY = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 0f, 125f)
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
