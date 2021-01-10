package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.confirmation_alert_dialog_2_butt.*

class PopUpDialog2Butt: DialogFragment() {
    //log tag definition
    var TAG: String = "droidsays"

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var currentuserID: String

    //callback to do work in the class that the dialog was initialised in
    var callback2butt: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()

        return inflater.inflate(R.layout.confirmation_alert_dialog_2_butt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        //casting values passed through a bundle in navigation
        val dialogtype = arguments?.getInt("dialogtype")
        val key = arguments?.getString("key")
        val visibility = arguments?.getBoolean("visibility")

        //setting dialog textfields based on type passed in navigation
        when(dialogtype){
            1 -> {
                titleFieldRW.text = getString(R.string.confirm_order)
                content.text = getString(R.string.question_confirm_order)
            }
            2 -> {
                titleFieldRW.text = getString(R.string.confirm_offer)
                content.text = getString(R.string.confirm_offer_text)
            }
            3 -> {
                titleFieldRW.text = getString(R.string.delete_offer)
                content.text = getString(R.string.delete_offer_content)
            }
            4 -> {
                titleFieldRW.text = getString(R.string.visibility_change)

                if (visibility == true) {
                    content.text = getString(R.string.visibility_change_invis)
                }else if (visibility == false) {
                    content.text = getString(R.string.visibility_change_vis)
                } else {
                    content.text = getString(R.string.unassigned)
                }
            }
            else -> {
                titleFieldRW.text = getString(R.string.unassigned)
                content.text = getString(R.string.unassigned)
            }
        }

        //button onclicklistener declaration
        yesButt.setOnClickListener {
            dismiss()
            //executes visibility change code if the visibility variable has a set value
            if (visibility != null) {
                changeVisibility(key, visibility)
            }
            callback2butt()
        }

        noButt.setOnClickListener {
            dismiss()
        }
    }

    //user product visibility change function
    fun changeVisibility(key: String?, visibility: Boolean){
        if (key != null) {
            database.child("products").child(key).child("visible").setValue(!visibility).addOnCompleteListener{
                    Log.i(TAG, "successfully changed visibility")
            }
        }
    }
}