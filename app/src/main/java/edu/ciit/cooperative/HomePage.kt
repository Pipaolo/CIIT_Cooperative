package edu.ciit.cooperative

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.w3c.dom.Text
import java.net.URI

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val string: String? = intent.getStringExtra("email")
        val userImage: String? = intent.getStringExtra("userImage")
        val signOut: MaterialButton = findViewById(R.id.home_btn_signOut)
        val textView: TextView = findViewById(R.id.home_tv_email)

        val appBarTitle: TextView = findViewById(R.id.toolbar_tv_title)
        val appBarImage: ImageView = findViewById(R.id.toolbar_iv_logo)

        if(string!!.contains("paolo.tolentino")){
            changeAppTitle(appBarTitle, "Admin")
        } else {
            changeAppTitle(appBarTitle, "Home")
        }

        textView.text = string

        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Signed Out Successfully!", Toast.LENGTH_LONG)
        }

        Picasso.get().isLoggingEnabled

        Picasso.get().load(userImage).resize(150, 150).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("Picasso:", "Getting Bitmap...${userImage}")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e("Picasso:", "Load Bitmap Failed!")
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                Log.d("Picasso:", "Load Bitmap Success!")
                val dr: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
                dr.cornerRadius = 100f
                appBarImage.setImageDrawable(dr)
            }
        })

    }

    private fun changeAppTitle(appBarTitle: TextView, string: String) {
        appBarTitle.text = string
    }
}
