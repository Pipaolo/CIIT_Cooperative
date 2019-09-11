package edu.ciit.cooperative.Fragments

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.ciit.cooperative.HomePage
import edu.ciit.cooperative.Models.Member

import edu.ciit.cooperative.R

class AddShareholderFragment : Fragment() {
    private var addShareholderFirestoreRecyclerAdapter: AddShareholderFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_shareholder, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.addShareholder_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        val rootRef = FirebaseFirestore.getInstance()
        val query = rootRef.collection("users").whereEqualTo("isShareholder", false)
        val options = FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member::class.java).build()

        (activity as HomePage).changeGraphVisibility(true)
        (activity as HomePage).changeHomeTitle(false, "Add Shareholder")

        addShareholderFirestoreRecyclerAdapter = AddShareholderFirestoreRecyclerAdapter(options)
        recyclerView.adapter = addShareholderFirestoreRecyclerAdapter

        return view
    }

    private inner class AddShareholderFragmentViewHolder internal constructor(private val view: View): RecyclerView.ViewHolder(view),
            View.OnClickListener {
        val textViewName : TextView = view.findViewById(R.id.addShareholder_card_tv_name)
        val imageViewProfileImage : ImageView = view.findViewById(R.id.addShareholder_card_iv_profileImage)
        var email: String = ""

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_Light)
                .setBackground(resources.getDrawable(R.drawable.customdialog,null))
                .setTitle("Add Shareholder?")
                .setMessage("Do you want to add ${textViewName.text} as a Shareholder?")
                .setPositiveButton("Yes") { _, _ ->
                    addShareholder(email)
                }
                .setNegativeButton("No", null)
                .show()
        }

        internal fun setMember(
            email: String,
            name: String,
            profileImage: String?
        ) {
            this.email = email
            textViewName.text = name
            imageViewProfileImage.load(profileImage) {
                placeholder(R.drawable.ic_person_black_24dp)
                crossfade(true)
                size(200)
            }
        }
    }

    private fun addShareholder(email: String){
        val user = FirebaseFirestore.getInstance().collection("users").document(email)
        user.update("isShareholder", true).addOnSuccessListener {
            Toast.makeText(context, "Shareholder Added!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Log.w("FireStore", "Failed to update user")
        }
    }

    override fun onDetach() {
        (activity as HomePage).changeGraphVisibility(false)
        (activity as HomePage).changeHomeTitle(true, "Add Shareholder")
        super.onDetach()
    }

    override fun onStart() {
        super.onStart()
        addShareholderFirestoreRecyclerAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (addShareholderFirestoreRecyclerAdapter != null) {
            addShareholderFirestoreRecyclerAdapter!!.stopListening()
        }
    }

    private inner class AddShareholderFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Member>) :
        FirestoreRecyclerAdapter<Member, AddShareholderFragmentViewHolder>(options) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AddShareholderFragmentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.add_shareholder_membercard, parent, false)
            return AddShareholderFragmentViewHolder(view)

        }
        override fun onBindViewHolder(p0: AddShareholderFragmentViewHolder, p1: Int, p2: Member) {
            p0.setMember(
                p2.email,
                p2.name,
                p2.profileImage
            )
        }
    }
}


