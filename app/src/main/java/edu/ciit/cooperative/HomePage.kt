package edu.ciit.cooperative

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import edu.ciit.cooperative.Fragments.ListMembersFragment
import edu.ciit.cooperative.Models.Menu
import edu.ciit.cooperative.RecyclerViews.MenuAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class HomePage : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val TAG = "HOMEPAGE:"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        FirebaseFirestore.setLoggingEnabled(true)
        val string: String? = intent.getStringExtra("email")
        val userImage: String? = intent.getStringExtra("userImage")
        val signOut: MaterialButton = findViewById(R.id.home_btn_signOut)

        val appBarMenu: ImageView = findViewById(R.id.toolbar_iv_menu)
        val appBarTitle: TextView = findViewById(R.id.toolbar_tv_title)
        val appBarLogo: ImageView = findViewById(R.id.toolbar_iv_logo)
        val appBarImage: ImageView = findViewById(R.id.toolbar_iv_profile)
        val graphSummary: GraphView = findViewById(R.id.home_graphView_summary)
        val recyclerViewMenu: RecyclerView = findViewById(R.id.home_recyclerView_menu)

        generateGraph(graphSummary)

        appBarLogo.load(R.drawable.logo) {
            crossfade(true)
            size(100, 100)
            transformations(CircleCropTransformation())
        }


        if (string!!.contains("paolo.tolentino")) {
            changeAppTitle(appBarTitle, appBarImage)
            appBarImage.load(userImage) {
                crossfade(true)
                size(200, 200)
                transformations(CircleCropTransformation())
            }
            generateUI(true, recyclerViewMenu)
        } else {
            appBarImage.load(userImage) {
                crossfade(true)
                size(200, 200)
                transformations(CircleCropTransformation())
            }
            changeAppTitle(appBarTitle, appBarImage)
        }

        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Signed Out Successfully!", Toast.LENGTH_LONG)
        }

    }

    private fun generateGraph(graph: GraphView) {
        val loansSeries = LineGraphSeries(
            arrayOf(
                DataPoint(1.0, 20.0),
                DataPoint(3.0, 10.0),
                DataPoint(3.0, 30.0),
                DataPoint(5.0, 20.0),
                DataPoint(6.0, 12.0)
            )
        )

        loansSeries.title = "Loans"
        loansSeries.color = resources.getColor(R.color.colorPlum, null)
        loansSeries.isDrawBackground = true
        loansSeries.backgroundColor = Color.argb(50, 224, 140, 192)
        loansSeries.thickness = 10

        val shareSeries = LineGraphSeries(
            arrayOf(
                DataPoint(1.0, 3.0),
                DataPoint(2.0, 5.0),
                DataPoint(3.0, 3.0),
                DataPoint(5.0, 12.0),
                DataPoint(6.0, 24.0)
            )
        )

        shareSeries.title = "Shares"
        shareSeries.color = resources.getColor(R.color.colorDeepPink, null)
        shareSeries.isDrawBackground = true
        shareSeries.backgroundColor = Color.argb(50, 229, 30, 173)
        shareSeries.thickness = 10

        graph.addSeries(loansSeries)
        graph.addSeries(shareSeries)
        graph.legendRenderer.isVisible = true
        graph.legendRenderer.align = LegendRenderer.LegendAlign.MIDDLE
        graph.legendRenderer.textColor = resources.getColor(android.R.color.white, null)
        graph.legendRenderer.backgroundColor = resources.getColor(android.R.color.transparent, null)
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        graph.gridLabelRenderer.isVerticalLabelsVisible = false
        graph.gridLabelRenderer.gridColor = resources.getColor(android.R.color.transparent, null)
    }

    private fun generateUI(isAdmin: Boolean, menu: RecyclerView) {
        val menuList = ArrayList<Menu>()
        val colorList = ArrayList<Int>()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        if (isAdmin) {
            menuList.add(Menu("Members"))
            menuList.add(Menu("Members/Shares"))
            menuList.add(Menu("Loans"))
            menuList.add(Menu("Shares"))
            menuList.add(Menu("Loan Requests"))

            colorList.add(Color.argb(215, 108, 24, 164))
            colorList.add(Color.argb(215, 56, 39, 180))
            colorList.add(Color.argb(215, 224, 140, 192))
            colorList.add(Color.argb(215, 108, 24, 164))
            colorList.add(Color.argb(215, 56, 39, 180))

        } else {

        }
        val adapter = MenuAdapter(
            this,
            menuList,
            colorList,
            { menuItem: Menu -> menuItemClicked(menuItem) })
        menu.setHasFixedSize(true)
        menu.layoutManager = layoutManager
        menu.adapter = adapter
    }

    private fun menuItemClicked(menuItem: Menu) {

        when (menuItem.title) {
            "Members" -> listMembers()
            "Members/Shares" -> addMembers()
            "Loans" -> Log.d(TAG, "Loans")
            "Shares" -> Log.d(TAG, "Shares")
            "Loan Requests" -> Log.d(TAG, "Loan Requests")
        }
    }


    private fun listMembers() {
        val listMemberFragment = ListMembersFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_listMembers, listMemberFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        toast("List Members!")
    }

    private fun addMembers() {
        val customDialog = Dialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(R.layout.custom_dialog_add_member_menu)
        customDialog.setCancelable(false)

        val closeBtn: ImageView = customDialog.findViewById(R.id.home_customDialog_close)
        val textViewAddMember: TextView = customDialog.findViewById(R.id.home_customDialog_tv_addMember)
        val textViewAddShare: TextView = customDialog.findViewById(R.id.home_customDialog_tv_addShares)

        textViewAddShare.setOnClickListener {
            customDialog.dismiss()
            customDialog.setContentView(R.layout.custom_dialog_add_shares)

            val cancelBtn: Button = customDialog.findViewById(R.id.custom_dialog_add_shares_btn_cancel)
            val submitBtn: Button = customDialog.findViewById(R.id.custom_dialog_add_shares_btn_submit)
            val email: EditText = customDialog.findViewById(R.id.custom_dialog_add_shares_et_email)
            val amount: EditText = customDialog.findViewById(R.id.custom_dialog_add_shares_et_amount)

            submitBtn.setOnClickListener {
                val batch = db.batch()
                val user = db.collection("users").whereEqualTo("email", email.text.toString()).get()
                    .addOnSuccessListener { documents ->
                        var docId: DocumentReference? = null
                        for (document in documents) {
                            if (document.data["email"].toString().equals(email.text.toString())) {
                                docId = document.reference
                                break
                            }
                        }

                        if (docId != null) {
                            batch.update(docId, "totalContributions", FieldValue.increment(1))
                            batch.update(docId, "totalShares", FieldValue.increment(amount.text.toString().toDouble()))
                            batch.commit().addOnSuccessListener {
                                toast("Added ShareHolder!")
                                customDialog.dismiss()
                            }.addOnFailureListener {
                                Log.w("FireStore: ", "Document Failed To Update!")
                            }
                        } else {

                            toast("User does not exists!")
                        }

                    }.addOnFailureListener {
                    Log.w("FireStore: ", "Document Failed To Update!")
                }
            }

            cancelBtn.setOnClickListener {
                customDialog.dismiss()
            }

            customDialog.show()

        }

        textViewAddMember.setOnClickListener {
            customDialog.dismiss()
            customDialog.setContentView(R.layout.custom_dialog_add_member)

            val cancelBtn: Button = customDialog.findViewById(R.id.custom_dialog_add_member_btn_cancel)
            val submitBtn: Button = customDialog.findViewById(R.id.custom_dialog_add_member_btn_submit)
            val email: EditText = customDialog.findViewById(R.id.custom_dialog_add_member_et_email)
            val firstName: EditText = customDialog.findViewById(R.id.custom_dialog_add_member_et_firstName)
            val middleName: EditText = customDialog.findViewById(R.id.custom_dialog_add_member_et_middleName)
            val lastName: EditText = customDialog.findViewById(R.id.custom_dialog_add_member_et_lastName)

            cancelBtn.setOnClickListener {
                customDialog.dismiss()
            }

            submitBtn.setOnClickListener {

                if (email.text.toString().contains("@ciit.edu.ph")) {
                    createUserAccount(
                        email.text.toString(),
                        "${firstName.text} ${middleName.text[0].toUpperCase()}. ${lastName.text}",
                        null,
                        null
                    )
                    customDialog.dismiss()
                } else {
                    toast("Please Use CIIT Email!")
                }
            }
            customDialog.show()
        }

        closeBtn.setOnClickListener {

            customDialog.dismiss()
        }

        customDialog.setOnShowListener {
            //            memberFirestoreRecyclerAdapter!!.startListening()
        }

        customDialog.setOnDismissListener {
            //            memberFirestoreRecyclerAdapter!!.stopListening()
        }

        customDialog.show()
    }

    private fun createUserAccount(email: String, name: String, profileImage: String?, id: String?) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users")
        var isUserExists = false

        doAsync {
            docRef.whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.id == email) {
                        isUserExists = true
                        break
                    }
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Exception: $e")
            }

            uiThread {
                if (!isUserExists) {
                    val user = hashMapOf(
                        "id" to id,
                        "email" to email,
                        "name" to name,
                        "profileImage" to profileImage,
                        "password" to "123",
                        "ableToLoan" to false,
                        "totalLoans" to 0.0,
                        "totalShare" to 0.0,
                        "totalContributions" to 1
                    )
                    docRef.document(email).set(user)
                        .addOnSuccessListener { toast("Member Added!") }
                        .addOnFailureListener { Log.w(TAG, "Error adding document") }
                } else {
                    toast("Member Exists!")
                }
            }
        }
    }

    private fun changeAppTitle(appBarTitle: TextView, profileImage: ImageView) {
        appBarTitle.visibility = View.INVISIBLE
        profileImage.visibility = View.VISIBLE
    }
}
