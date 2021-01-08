package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import com.example.kvaldarbs.offerflow.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.confirmation_alert_dialog_2_butt.*
import kotlinx.android.synthetic.main.dialog_critic.*

var TAG:String = "droidsays"

class PopUpDialog2Butt: DialogFragment() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var currentuserID: String

    var aaa: () -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()

        return inflater.inflate(R.layout.confirmation_alert_dialog_2_butt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        //val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialogtype = arguments?.getInt("dialogtype")
        val key = arguments?.getString("key")
        val visibility = arguments?.getBoolean("visibility")

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

        yesButt.setOnClickListener {
//            val bundle = bundleOf("entrytext" to enterNameField.text.toString())
            Log.i(TAG, "on view created dialog closure part")
            dismiss()
            if (visibility != null) {
                changeVisibility(key, visibility)
            }
            aaa()
        }

        noButt.setOnClickListener {
            dismiss()
        }
    }


    fun changeVisibility(key: String?, visibility: Boolean){
        if (key != null) {
            database.child("products").child(key).child("visible").setValue(!visibility).addOnCompleteListener{
                    Log.i(com.example.kvaldarbs.dialogs.TAG, "successfully changed visibility")


            }
        }
    }
}