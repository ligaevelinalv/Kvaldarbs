package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.confirmation_alert_dialog_2_butt.*
import kotlinx.android.synthetic.main.confirmation_alert_dialog_2_butt.titleFieldRW
import kotlinx.android.synthetic.main.dialog_critic.*

class CriticDialog : DialogFragment() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var currentuserID: String
    lateinit var critic: String

    var callbackToParent: () -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()


        return inflater.inflate(R.layout.dialog_critic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialogtype = arguments?.getInt("dialogtype")
        val key = arguments?.getString("key")

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

        okButt.setOnClickListener {
            critic = adminCriticField.text.toString()

            dismiss()
            changeVisibility(key, critic)
            callbackToParent()

        }

        cancelButt.setOnClickListener {
            dismiss()
        }
    }

    fun changeVisibility(key: String?, critic: String){
            if (key != null) {
                database.child("products").child(key).child("visible").setValue(false).addOnSuccessListener {
                    database.child("products").child(key).child("admincritic").setValue(critic)

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Something went wrong, check your internet connection.",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

}