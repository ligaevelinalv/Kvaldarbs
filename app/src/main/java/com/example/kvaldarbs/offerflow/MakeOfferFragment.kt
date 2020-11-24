package com.example.kvaldarbs.offerflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_detail.*



class MakeOfferFragment : Fragment() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_makeoffer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val ree = PopUpDialog2Butt()
        val bundle = Bundle()
        ree.aaa = {
            navigateToConfirm()
        }

        offerButton.setOnClickListener {
            //onAlertDialog(view)

            bundle.putInt("dialogtype", 2)
            ree.arguments = bundle
            ree.show(parentFragmentManager, "")

        }
    }

    fun navigateToConfirm(){
        findNavController().navigate(R.id.action_makeOfferFragment_to_offerConfirmationFragment)

    }

}