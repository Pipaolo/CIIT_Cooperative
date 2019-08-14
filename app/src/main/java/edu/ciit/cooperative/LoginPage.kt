package edu.ciit.cooperative

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jetbrains.anko.toast

class LoginPage : AppCompatActivity() {
    //Sign In
    val db = FirebaseFirestore.getInstance()
    val RC_SIGN_IN: Int = 1
    val TAG = "LOGIN"
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    //End of Google Sign In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        firebaseAuth = FirebaseAuth.getInstance()
        configureGoogleSignIn() //Initialize Google Sign In

        val username: TextInputEditText? = findViewById(R.id.login_et_username)
        val password: TextInputEditText? = findViewById(R.id.login_et_password)
        val submit: MaterialButton = findViewById(R.id.login_btn_signIn)
        val googleSignIn: SignInButton = findViewById(R.id.login_btn_googleSignIn)
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

        googleSignIn.setOnClickListener {
            signIn()
        }

    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In Failed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email!!.contains("ciit.edu.ph")) {
            goToHomePage(user)
        } else {
            mGoogleSignInClient.signOut()
        }
    }

    private fun goToHomePage(user: FirebaseUser) {
        val intent = Intent(this, HomePage::class.java)

        Toast.makeText(this, "Welcome ${user.displayName}!", Toast.LENGTH_LONG).show()
        intent.putExtra("email", user.email)
        startActivity(intent)
        finish()
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut()
    }

    private fun firebaseAuthWithGoogle(user: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(user?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if(user!!.email!!.contains("@ciit.edu.ph")){
                    Toast.makeText(this, "Welcome ${user.displayName}!", Toast.LENGTH_LONG).show()
                    intent.putExtra("email", user.email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Please Use CIIT Email!", Toast.LENGTH_LONG).show()
                    signOut()
                    Log.d(TAG, "Signed Out!")
                }
                Log.d(TAG, "Success!")
            } else {
                Log.e(TAG, "Failed!")
            }
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



