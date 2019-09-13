package edu.ciit.cooperative.Fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.ciit.cooperative.HomePage
import edu.ciit.cooperative.Models.Member
import edu.ciit.cooperative.R
import kotlinx.android.synthetic.main.custom_dialog_member_status.*
import kotlinx.android.synthetic.main.fragment_list_members.view.*
import kotlinx.android.synthetic.main.home_member_card.view.*

class ListMembersFragment : Fragment() {
    private var memberFirestoreRecyclerAdapter: MemberFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_members, container, false)
        val recyclerView: RecyclerView = view.fragment_listMembers_recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val rootRef = FirebaseFirestore.getInstance()
        val query = rootRef.collection("users").orderBy("email", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member::class.java).build()

        (activity as HomePage).showSignOutButton(true)

        memberFirestoreRecyclerAdapter = MemberFirestoreRecyclerAdapter(options)
        recyclerView.adapter = memberFirestoreRecyclerAdapter
        // Inflate the layout f this fragment
        return view
    }

    private inner class MemberViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view),
        View.OnLongClickListener {
        val isAbleToLoan: SwitchCompat = view.home_member_card_switch_loan
        val textviewName: TextView = view.home_member_card_tv_name
        val imageProfile: ImageView = view.home_member_card_iv_profileImage

        var profileImage: String? = null
        var email = ""
        var name = ""
        var password = ""
        var ableToLoan = false
        var totalLoans = 0.0
        var totalShares = 0.0
        var totalContributions = 0

        init {
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            memberStatus(profileImage, email, name, password, ableToLoan, totalLoans, totalShares, totalContributions)
            return true
        }

        internal fun setMember(
            email1: String,
            id: String?,
            name1: String,
            password1: String,
            profileImage1: String?,
            ableToLoan1: Boolean,
            totalLoans1: Double,
            totalShares1: Double,
            totalContributions1: Int
        ) {
            this.email = email1
            this.name = name1
            this.password = password1
            this.profileImage = profileImage1
            this.ableToLoan = ableToLoan1
            this.totalLoans = totalLoans1
            this.totalShares = totalShares1
            this.totalContributions = totalContributions1

            textviewName.text = name1
            isAbleToLoan.isChecked = ableToLoan1

            isAbleToLoan.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    val user = FirebaseFirestore.getInstance().collection("users").document(email1)
                    user.update("isAbleToLoan", true).addOnSuccessListener {
                        Toast.makeText(context, "$name Can now Loan!", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener { Log.w("FireStore: ", "Failed to update user") }
                } else {
                    val user = FirebaseFirestore.getInstance().collection("users").document(email1)
                    user.update("isAbleToLoan", false).addOnSuccessListener {
                        Toast.makeText(context, "$name Cannot Loan!", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener { Log.w("FireStore: ", "Failed to update user") }
                }
            }

            imageProfile.load(profileImage1) {
                placeholder(R.drawable.ic_person_black_24dp)
                crossfade(true)
                size(200)
            }
        }
    }

    private inner class MemberFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Member>) :
        FirestoreRecyclerAdapter<Member, MemberViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.home_member_card, parent, false)
            return MemberViewHolder(view)
        }

        override fun onBindViewHolder(p0: MemberViewHolder, p1: Int, p2: Member) {

            p0.setMember(
                p2.email,
                p2.id,
                p2.name,
                p2.password,
                p2.profileImage,
                p2.isAbleToLoan,
                p2.totalLoans,
                p2.totalShares,
                p2.totalContributions
            )
        }
    }

    override fun onStart() {
        super.onStart()
        memberFirestoreRecyclerAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (memberFirestoreRecyclerAdapter != null) {
            memberFirestoreRecyclerAdapter!!.stopListening()
        }
    }

    private fun memberStatus(
        profileImage: String?,
        email: String,
        name: String,
        password: String,
        ableToLoan: Boolean,
        totalLoans: Double,
        totalShares: Double,
        totalContributions: Int
    ) {
        val customDialog = Dialog(context!!)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(R.layout.custom_dialog_member_status)
        customDialog.setCancelable(true)


        val imageViewProfile: ImageView = customDialog.customDialog_member_status_profileImage
        val textViewEmail: TextView = customDialog.customDialog_member_status_email
        val textViewName: TextView = customDialog.customDialog_member_status_name
        val textViewPassword: TextView = customDialog.customDialog_member_status_password
        val textViewIsAbleToLoan: TextView = customDialog.customDialog_member_status_isAbleToLoan
        val textViewTotalLoan: TextView = customDialog.customDialog_member_status_totalLoan
        val textViewTotalShare: TextView = customDialog.customDialog_member_status_totalShare
        val textViewTotalContribution: TextView = customDialog.customDialog_member_status_contribution

        if (profileImage != null) {
            imageViewProfile.load(profileImage) {
                crossfade(true)
                size(100)
                transformations(CircleCropTransformation())
            }
        } else {
            imageViewProfile.load(R.drawable.ic_person_black_24dp) {
                crossfade(true)
                size(100)
                transformations(CircleCropTransformation())
            }
        }

        textViewEmail.text = activity!!.applicationContext.getString(R.string.member_email, email)
        textViewName.text = activity!!.applicationContext.getString(R.string.member_name, name)
        textViewPassword.text = activity!!.applicationContext.getString(R.string.member_password, password)
        textViewIsAbleToLoan.text = activity!!.applicationContext.getString(R.string.member_isAbleToLoan, ableToLoan.toString())
        textViewTotalLoan.text = activity!!.applicationContext.getString(R.string.member_totalLoan, totalLoans.toString())
        textViewTotalShare.text = activity!!.applicationContext.getString(R.string.member_totalShare, totalShares.toString())
        textViewTotalContribution.text = activity!!.applicationContext.getString(R.string.member_totalContribution, totalContributions.toString())

        customDialog.show()

    }

    override fun onDetach() {
        super.onDetach()
        (activity as HomePage).showSignOutButton(false)
    }

}
