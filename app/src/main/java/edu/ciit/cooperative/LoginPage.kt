package edu.ciit.cooperative

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlinx.android.synthetic.main.activity_login_page.*
import org.jetbrains.anko.doAsync

class LoginPage : AppCompatActivity() {
    //Sign In
    var db = FirebaseFirestore.getInstance()
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


        username!!.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (username.nonEmpty()) {
                    if (!username.text.toString().contains("ciit.edu.ph")) {
                        Log.d("tag", username.text.toString())
                        login_layout_username.error = "Use CIIT Email"
                    } else {
                        login_layout_username.isErrorEnabled = false
                    }

                } else {
                    login_layout_username.error = "Field cannot be empty!"
                }

            } else {

            }
        }

        password!!.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            }
        }


        submit.setOnClickListener {
            doAsync { startLogin(username?.text.toString(), password?.text.toString()) }
        }
        googleSignIn.setOnClickListener {
            doAsync {
                signIn()
            }
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
        intent.putExtra("userImage", user.photoUrl.toString())
        startActivity(intent)
        finish()
    }

    private fun goToHomePage(user: DocumentSnapshot) {
        val intent = Intent(this, HomePage::class.java)

        Toast.makeText(this, "Welcome ${user["name"]}!", Toast.LENGTH_LONG).show()
        intent.putExtra("email", user["email"].toString())
        intent.putExtra("isAbleToLoan", user["isAbleToLoan"].toString().toBoolean())
        intent.putExtra("userImage", user["profileImage"].toString())
        startActivity(intent)
        finish()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut()
    }

    private fun firebaseAuthWithGoogle(user: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(user?.idToken, null)
        Log.d(TAG, credential.toString())
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (user!!.email!!.contains("@ciit.edu.ph")) {
                    createUserAccount(
                        user.email.toString(),
                        user.displayName.toString(),
                        user.photoUrl.toString(),
                        user.idToken
                    )
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

    private fun startLogin(email: String, password: String) {
        db = FirebaseFirestore.getInstance()

        db.collection("users").whereEqualTo("email", email).whereEqualTo("password", password).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(document["id"].toString(), null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            Toast.makeText(
                                this,
                                "Welcome ${document.data["name"]}!",
                                Toast.LENGTH_LONG
                            ).show()
                            goToHomePage(document)
                        }
                }
                if(documents.isEmpty){
                    Toast.makeText(
                        this,
                        "User not found!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun createUserAccount(email: String, name: String, profileImage: String, id: String?) {
        val user = hashMapOf(
            "id" to id,
            "email" to email,
            "name" to name,
            "profileImage" to profileImage,
            "password" to "123"
        )
        db.collection("users").document(email).set(user).addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot added!")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }
}

class BackgroundView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val linePaint = Paint()
    private val lineLightBlue = Path()
    private val lineDarkBlue = Path()

    override fun onDraw(canvas: Canvas?) {
        linePaint.color = resources.getColor(R.color.colorPrimary, null)
        lineDarkBlue.moveTo(width.toFloat(), height * 0.3f)
        lineDarkBlue.lineTo(width.toFloat(), height.toFloat())
        lineDarkBlue.lineTo(0.0f, height.toFloat())

        canvas!!.drawPath(lineDarkBlue, linePaint)

        linePaint.color = resources.getColor(R.color.colorSecondary, null)
        lineLightBlue.moveTo(width * 0.6f, 0.0f)
        lineLightBlue.lineTo(width.toFloat(), 0.0f)
        lineLightBlue.lineTo(width.toFloat(), height * 0.3f)

        canvas!!.drawPath(lineLightBlue, linePaint)

        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)


        invalidate()
        setMeasuredDimension(widthSize, heightSize)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}



