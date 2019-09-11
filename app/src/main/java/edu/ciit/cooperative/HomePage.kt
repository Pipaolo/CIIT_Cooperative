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
import edu.ciit.cooperative.Fragments.AddMemberFragment
import edu.ciit.cooperative.Fragments.AddShareholderFragment
import edu.ciit.cooperative.Fragments.ListMembersFragment
import edu.ciit.cooperative.Models.Menu
import edu.ciit.cooperative.RecyclerViews.MenuAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class HomePage : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val TAG = "HOMEPAGE:"
    var homeTitle: TextView? = null
    var graphSummary: GraphView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        FirebaseFirestore.setLoggingEnabled(true)
        val string: String? = intent.getStringExtra("email")
        val userImage: String? = intent.getStringExtra("userImage")
        val signOut: MaterialButton = findViewById(R.id.home_btn_signOut)

        val appBarMenu: ImageView = findViewById(R.id.toolbar_iv_menu)
        val appBarTitle: TextView = findViewById(R.id.toolbar_tv_title)
        val appBarImage: ImageView = findViewById(R.id.toolbar_iv_profile)
        val appBarUserStatus: TextView = findViewById(R.id.toolbar_tv_userStatus)
        val recyclerViewMenu: RecyclerView = findViewById(R.id.home_recyclerView_menu)
        graphSummary = findViewById(R.id.home_graphView_summary)
        homeTitle = findViewById(R.id.home_panel_title)

        generateGraph(graphSummary)

        appBarMenu.visibility = View.VISIBLE

        if (string!!.contains("paolo.tolentino")) {
            appBarUserStatus.visibility = View.VISIBLE
            appBarImage.load(userImage) {
                crossfade(true)
                size(100, 100)
                transformations(CircleCropTransformation())
            }
            generateUI(true, recyclerViewMenu)
        } else {
            appBarImage.load(userImage) {
                crossfade(true)
                size(100, 100)
                transformations(CircleCropTransformation())
            }
        }
        changeAppTitle(appBarTitle, appBarImage)

        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Signed Out Successfully!", Toast.LENGTH_LONG)
        }

    }

    private fun generateGraph(graph: GraphView?) {
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

        graph!!.addSeries(shareSeries)
        graph.addSeries(loansSeries)
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
            menuList.add(Menu("Add Member"))
            menuList.add(Menu("Add Shareholder"))
            menuList.add(Menu("Loans"))
            menuList.add(Menu("Shares"))
            menuList.add(Menu("Loan Requests"))

            colorList.add(Color.argb(215, 108, 24, 164))
            colorList.add(Color.argb(215, 56, 39, 180))
            colorList.add(Color.argb(215, 224, 140, 192))
            colorList.add(Color.argb(215, 108, 24, 164))
            colorList.add(Color.argb(215, 56, 39, 180))
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
            "Add Member" -> addMembers()
            "Add Shareholder" -> addShareholder()
            "Loans" -> Log.d(TAG, "Loans")
            "Shares" -> Log.d(TAG, "Shares")
            "Loan Requests" -> Log.d(TAG, "Loan Requests")
        }
    }


    private fun listMembers() {
        val listMemberFragment = ListMembersFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_frame, listMemberFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        toast("List Members!")
    }

    private fun addShareholder() {
        val addShareholderFragment = AddShareholderFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_frame, addShareholderFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


    private fun addMembers() {
        val addMemberFragment = AddMemberFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_frame, addMemberFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun createUserAccount(email: String, name: String, profileImage: String?, id: String?) {
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
    fun changeGraphVisibility(isHidden : Boolean){
        if(!isHidden){
            graphSummary!!.visibility = View.VISIBLE
        } else {
            graphSummary!!.visibility = View.INVISIBLE
        }
    }

    fun changeHomeTitle(isHidden : Boolean, title : String){
        homeTitle!!.text = title
        if(!isHidden){
            homeTitle!!.visibility = View.VISIBLE
        } else {
            homeTitle!!.visibility = View.INVISIBLE
        }
    }


    private fun changeAppTitle(appBarTitle: TextView, profileImage: ImageView) {
        appBarTitle.visibility = View.INVISIBLE
        profileImage.visibility = View.VISIBLE
    }
}
