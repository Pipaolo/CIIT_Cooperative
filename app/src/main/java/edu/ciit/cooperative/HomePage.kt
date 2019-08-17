package edu.ciit.cooperative

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import edu.ciit.cooperative.Models.Member
import edu.ciit.cooperative.Models.Menu
import org.jetbrains.anko.find

class HomePage : AppCompatActivity() {

    private var memberFirestoreRecyclerAdapter: MemberFirestoreRecyclerAdapter? = null

    fun addMembers() {
        val customDialog = Dialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(R.layout.home_custom_dialog_add_user)

        val closeBtn: ImageView = customDialog.findViewById(R.id.home_customDialog_close)
        val recyclerView: RecyclerView = customDialog.findViewById(R.id.home_customDialog_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val rootRef = FirebaseFirestore.getInstance()
        val query = rootRef.collection("users").orderBy("email", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member::class.java).build()

        memberFirestoreRecyclerAdapter = MemberFirestoreRecyclerAdapter(options)
        recyclerView.adapter = memberFirestoreRecyclerAdapter

        closeBtn.setOnClickListener {

            customDialog.dismiss()
        }

        customDialog.setOnShowListener {
            memberFirestoreRecyclerAdapter!!.startListening()
        }

        customDialog.setOnDismissListener {
            memberFirestoreRecyclerAdapter!!.stopListening()
        }

        customDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val string: String? = intent.getStringExtra("email")
        val userImage: String? = intent.getStringExtra("userImage")
        val signOut: MaterialButton = findViewById(R.id.home_btn_signOut)


        val dashboardMenu: GridView = findViewById(R.id.home_gv_dashboard)
        val appBarTitle: TextView = findViewById(R.id.toolbar_tv_title)
        val appBarImage: ImageView = findViewById(R.id.toolbar_iv_logo)

        loadImage(userImage, appBarImage, 150, 150)

        if (string!!.contains("paolo.tolentino")) {
            changeAppTitle(appBarTitle, "Admin")
            generateUI(true, dashboardMenu)
        } else {
            changeAppTitle(appBarTitle, "Home")
            generateUI(false, dashboardMenu)
        }

        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Signed Out Successfully!", Toast.LENGTH_LONG)
        }

    }

    private fun generateUI(isAdmin: Boolean, menu: GridView) {
        val adapter: MenuAdapter?
        val menuList = ArrayList<Menu>()

        if (isAdmin) {
            menuList.add(Menu("Members"))
            menuList.add(Menu("Add Member"))
            menuList.add(Menu("Temp 1"))
            menuList.add(Menu("Temp 2"))
        } else {

        }

        adapter = MenuAdapter(this, menuList)
        menu.adapter = adapter
    }

    private fun loadImage(userImage: String?, imageView: ImageView, width: Int, height: Int) {
        Picasso.get().load(userImage).resize(width, height).into(object : Target {
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
                imageView.setImageDrawable(dr)
            }
        })
    }


    private inner class MemberViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        internal fun setMember(email: String, id: String, name: String, password: String, profileImage: String?) {
            val textView_email: TextView = view.findViewById(R.id.home_customDialog_tv_email)
            val textView_name: TextView = view.findViewById(R.id.home_customDialog_tv_name)
            val textView_password: TextView = view.findViewById(R.id.home_customDialog_tv_password)
            val imageView_profileImage: ImageView = view.findViewById(R.id.home_customDialog_iv_profileImage)

            textView_email.setText("Email: $email")
            textView_name.setText("Name: $name")
            textView_password.setText("Password: $password")
            loadImage(profileImage, imageView_profileImage, 150, 150)
        }
    }

    private inner class MemberFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Member>) :
        FirestoreRecyclerAdapter<Member, MemberViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.home_custom_dialog_member, parent, false)
            return MemberViewHolder(view)
        }

        override fun onBindViewHolder(p0: MemberViewHolder, p1: Int, p2: Member) {
            p0.setMember(p2.email, p2.id, p2.name, p2.password, p2.profileImage)

        }
    }

    private inner class MenuAdapter : BaseAdapter {
        var menuList = ArrayList<Menu>()
        var context: Context? = null

        constructor(context: Context, menuList: ArrayList<Menu>) : super() {
            this.menuList = menuList
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val menuTitle = this.menuList[p0]

            val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val menuView = inflator.inflate(R.layout.home_dashboard_menu, null)
            val cardView: CardView = p1!!.findViewById(R.id.home_cardView_dashboardMenu)
            val textView: TextView = p1.find(R.id.home_cardView_dashboardMenuTitle)

            textView.setText(menuTitle.title)

            cardView.setOnClickListener {
                when (textView.text) {
                    "Members" -> Toast.makeText(context, "Members", Toast.LENGTH_SHORT)
                    "Add Member" -> addMembers()
                }
            }

            return menuView
        }

        override fun getItem(p0: Int): Any {
            return menuList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return menuList.size
        }
    }

    private fun changeAppTitle(appBarTitle: TextView, string: String) {
        appBarTitle.text = string
    }
}
