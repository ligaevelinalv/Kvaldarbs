package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.content
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.titleFieldRW
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.yesButt

class PopUpDialog1Butt: DialogFragment() {
    //callback to do work in the class that the dialog was initialised in
    var callback1butt: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)
        return inflater.inflate(R.layout.confirmation_alert_dialog_1_butt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        //casting values passed through a bundle in navigation
        val dialogtype = arguments?.getInt("dialogtype")
        val resetEmail = arguments?.getString("reset")

        //setting dialog textfields based on type passed in navigation
        when(dialogtype){
            1 -> {
                titleFieldRW.text = getString(R.string.order_confirmed)
                content.text = getString(R.string.order_confirmed_message)
            }

            2 -> {
                titleFieldRW.text = getString(R.string.offer_confirmed)
                content.text = getString(R.string.offer_confirmed_text)
            }

            3 -> {
                titleFieldRW.text = getString(R.string.confirm_account_deletion)
                content.text = getString(R.string.delete_account)
            }

            4 -> {
                titleFieldRW.text = getString(R.string.password_reset)
                content.text ="A password reset link has been sent to " + resetEmail
            }

            else -> {
                titleFieldRW.text = getString(R.string.unassigned)
                content.text = getString(R.string.unassigned)
            }
        }

        //button onclicklistener declaration
        yesButt.setOnClickListener {
            dismiss()
            callback1butt()
        }
    }
}