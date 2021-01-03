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
import kotlinx.android.synthetic.main.dialog_reauthenticate.*

class PopUpDialog1Butt: DialogFragment() {

    var aaa: () -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)
        return inflater.inflate(R.layout.confirmation_alert_dialog_1_butt, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        //val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialogtype = arguments?.getInt("dialogtype")

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

            else -> {
                titleFieldRW.text = getString(R.string.unassigned)
                content.text = getString(R.string.unassigned)
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yesButt.setOnClickListener {
//            val bundle = bundleOf("entrytext" to enterNameField.text.toString())
            dismiss()
                aaa()
        }
    }
}