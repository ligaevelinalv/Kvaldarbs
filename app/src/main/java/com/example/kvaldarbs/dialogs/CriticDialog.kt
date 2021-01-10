package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_critic.*

class CriticDialog : DialogFragment() {
    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var currentuserID: String
    lateinit var reason: String

    //callback to do work in the class that the dialog was initialised in
    var callbackcritic: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()


        return inflater.inflate(R.layout.dialog_critic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        //casting values passed through a bundle in navigation
        val dialogtype = arguments?.getInt("dialogtype")
        val key = arguments?.getString("key")

        //setting dialog textfields based on type passed in navigation
        when(dialogtype){
            1 -> {
                adminTitleField.text = getString(R.string.visibility_change)
                adminContentField.text = getString(R.string.visibility_change_content)
            }

            else -> {
                adminTitleField.text = getString(R.string.unassigned)
                adminContentField.text = getString(R.string.unassigned)
            }
        }

        //button onclicklistener declaration
        okButt.setOnClickListener {
            reason = adminCriticField.text.toString()
            changeVisibility(key, reason)
        }

        cancelButt.setOnClickListener {
            dismiss()
        }
    }

    //administrator product visibility change function, displays error message if no reason is given for changing the visibility
    fun changeVisibility(key: String?, reason: String){
            if (key != null) {
                if (reason != "")  {
                    dismiss()
                    database.child("products").child(key).child("visible").setValue(false)
                    database.child("products").child(key).child("admincritic").setValue(reason)
                    callbackcritic()
                } else {
                    adminCriticField.error = "Please provide a reason for changing the visibility!"
                }

            }
    }

}