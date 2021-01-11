package com.example.kvaldarbs.offerflow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog1Butt
import com.example.kvaldarbs.mainpage.MainScreen

class OfferConfirmationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as OfferFlowScreen).supportActionBar?.title = ""

        return inflater.inflate(R.layout.fragment_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //dialog popup shows on startup
        val ree = PopUpDialog1Butt()
        val bundle = Bundle()
        ree.callback1butt = {
            navigateToConfirm()
        }

        bundle.putInt("dialogtype", 2)
        ree.arguments = bundle
        ree.show(parentFragmentManager, "")
    }

    fun navigateToConfirm(){
        (activity as OfferFlowScreen).finish()
    }

}
