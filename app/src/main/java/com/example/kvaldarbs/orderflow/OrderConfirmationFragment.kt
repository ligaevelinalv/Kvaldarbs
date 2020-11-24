package com.example.kvaldarbs.orderflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog1Butt

class OrderConfirmationFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirmation, container, false)
    }

    override fun onStart() {
        super.onStart()

        val ree = PopUpDialog1Butt()
        val bundle = Bundle()
        ree.aaa = {
            navigateToConfirm()
        }

        bundle.putInt("dialogtype", 1)
        ree.arguments = bundle
        ree.show(parentFragmentManager, "")

    }

    fun navigateToConfirm(){
        findNavController().navigate(R.id.action_confirmationFragment_to_frontpageFragment)

    }


}
