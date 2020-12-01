package com.example.kvaldarbs.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import com.example.kvaldarbs.offerflow.TAG
import kotlinx.android.synthetic.main.confirmation_alert_dialog_2_butt.*

var TAG:String = "monitor"

class PopUpDialog2Butt: DialogFragment() {

    var aaa: () -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)
        return inflater.inflate(R.layout.confirmation_alert_dialog_2_butt, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        //val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialogtype = arguments?.getInt("dialogtype")

        when(dialogtype){
            1 -> {
                titleFieldRW.text = getString(R.string.confirm_order)
                content.text = getString(R.string.question_confirm_order)
            }
            2 -> {
                titleFieldRW.text = getString(R.string.confirm_offer)
                content.text = getString(R.string.confirm_offer_text)
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
            Log.i(TAG, "on view created dialog closure part")
            dismiss()
                aaa()


        }

        noButt.setOnClickListener {
            dismiss()
        }
    }

}