package edu.ciit.cooperative.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.ciit.cooperative.Models.Member

import edu.ciit.cooperative.R

class AddShareholderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_shareholder, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.addShareholder_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        val rootRef = FirebaseFirestore.getInstance()
        val query = rootRef.collection("users").orderBy("email", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member::class.java).build()

        return view
    }

    private inner class AddShareholderFragmentViewHolder internal constructor(private val view: View): RecyclerView.ViewHolder(view),
            View.OnClickListener {
        val textViewName : TextView = view.findViewById(R.id.addShareholder_card_tv_name)
        val imageViewProfileImage : ImageView = view.findViewById(R.id.addShareholder_card_iv_profileImage)


        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        internal fun setMember(
            name: String,
            profileImage: String?
        ) {
            textViewName.text = name
            imageViewProfileImage.load(profileImage) {
                placeholder(R.drawable.ic_person_black_24dp)
                crossfade(true)
                size(200)
            }

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
                p2.name,
                p2.profileImage
            )
        }
    }

}


