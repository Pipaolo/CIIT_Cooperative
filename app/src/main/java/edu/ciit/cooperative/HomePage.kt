package edu.ciit.cooperative

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val string: String? = intent.getStringExtra("email")
        val signOut: MaterialButton = findViewById(R.id.home_btn_signOut)
        val appBarTitle: TextView = findViewById(R.id.toolbar_tv_title)
        val textView: TextView = findViewById(R.id.home_tv_email)

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
    }

    private fun changeAppTitle(appBarTitle: TextView, string: String) {
        appBarTitle.setText(string)
    }
}
