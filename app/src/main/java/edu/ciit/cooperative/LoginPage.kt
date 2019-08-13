package edu.ciit.cooperative

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class LoginPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)


        val username: TextInputEditText? = findViewById(R.id.login_et_username)
        val password: TextInputEditText? = findViewById(R.id.login_et_password)
        val submit: MaterialButton = findViewById(R.id.login_btn_signIn)

        val toolBar: Toolbar? = findViewById(R.id.toolbar_custom)
        setSupportActionBar(toolBar)
        val logo: ImageView = findViewById(R.id.toolbar_iv_logo)

        Picasso.get().load(R.drawable.logo).resize(150, 150).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("Picasso:", "Getting Bitmap...")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e("Picasso:", "Load Bitmap Failed!")
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                Log.d("Picasso:", "Load Bitmap Success!")
                val dr: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
                dr.setCornerRadius(10.0f)
                logo.setImageDrawable(dr)
            }
        })

        submit.setOnClickListener {
            startLogin(username?.text.toString(), password?.text.toString())
        }
    }

    private fun startLogin(username: String, password: String) {
        if (username.equals("admin") && password.equals("123")) {
            Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
        }
    }
}
