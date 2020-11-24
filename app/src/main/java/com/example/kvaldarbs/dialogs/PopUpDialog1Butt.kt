package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.*
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.content
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.title
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.yesButt
import kotlinx.android.synthetic.main.confirmation_alert_dialog_2_butt.*

class PopUpDialog1Butt: DialogFragment() {

    var aaa: () -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog);
        return inflater.inflate(R.layout.confirmation_alert_dialog_1_butt, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialogtype = arguments?.getInt("dialogtype")?.toInt()

        when(dialogtype){
            1 -> {
                title.text = getString(R.string.order_confirmed)
                content.text = getString(R.string.order_confirmed_message)
            }

            2 -> {
                title.text = getString(R.string.offer_confirmed)
                content.text = getString(R.string.offer_confirmed_text)
            }

            else -> {
                title.text = getString(R.string.unassigned)
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