package edu.ciit.cooperative.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import edu.ciit.cooperative.HomePage
import edu.ciit.cooperative.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class AddMemberFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_member, container, false)
        val editTextEmail: EditText = view.findViewById(R.id.addMember_et_email)
        val editTextFName: EditText = view.findViewById(R.id.addMember_et_firstName)
        val editTextMName: EditText = view.findViewById(R.id.addMember_et_middleName)
        val editTextLName: EditText = view.findViewById(R.id.addMember_et_lastName)
        val btnSubmit: Button = view.findViewById(R.id.addMember_btn_submit)


        (activity as HomePage).changeGraphVisibility(true)
        (activity as HomePage).changeHomeTitle(false, "Add Member")

        btnSubmit.setOnClickListener {
            val email = editTextEmail.text.toString()
            val fName = editTextFName.text.toString()
            val mName = editTextMName.text.toString()
            val lName = editTextLName.text.toString()
            val fullName = "$fName $mName $lName"
            if (!editTextEmail.text.contains("@ciit.edu.ph")) {
                Toast.makeText(context, "Invalid Email! Use CIIT Email", Toast.LENGTH_LONG).show()
            } else {
                doAsync {
                    (activity as HomePage).createUserAccount(email, fullName, null, null)
                    uiThread {
                        fragmentManager!!.popBackStack()
                    }

                }
            }
        }
        return view
    }

    override fun onDetach() {
        (activity as HomePage).changeGraphVisibility(false)
        (activity as HomePage).changeHomeTitle(true, "Add Member")

        super.onDetach()
    }

}
