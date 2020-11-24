package com.example.kvaldarbs.orderflow

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt

import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {
    lateinit var passedval: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passedval = arguments?.getString("entrytext").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message = passedval
        data.text = message

        val ree = PopUpDialog2Butt()
        val bundle = Bundle()
        ree.aaa = {
            navigateToConfirm()
        }

        offerButton.setOnClickListener {
            //onAlertDialog(view)

            bundle.putInt("dialogtype", 1)
            ree.arguments = bundle
            ree.show(parentFragmentManager, "")

        }
    }

    fun navigateToConfirm(){
        findNavController().navigate(R.id.action_detailFragment_to_confirmationFragment)

    }

//    fun onAlertDialog(view: View) {
//        //Instantiate builder variable
//        val builder = AlertDialog.Builder(view.context)
//
//        // set title
//        builder.setTitle("CONFIRM ORDER")
//
//        //set content area
//        builder.setMessage("Do you wish to order this?")
//
//        //set negative button
//        builder.setPositiveButton(
//                "YES") { dialog, id ->
//            // User clicked Update Now button
//            Toast.makeText(this.requireContext(), "Updating your device", Toast.LENGTH_SHORT).show()
//        }
//
//        //set positive button
//        builder.setNegativeButton(
//                "CANCEL") { dialog, id ->
//            // User cancelled the dialog
//        }
//
//        //set neutral button
////        builder.setNeutralButton("Reminder me latter") {dialog, id->
////            // User Click on reminder me latter
////        }
//        builder.show()
//    }
}