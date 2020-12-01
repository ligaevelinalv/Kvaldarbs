package com.example.kvaldarbs.orderflow

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.dialogs.TAG
import com.example.kvaldarbs.mainpage.productList
import com.example.kvaldarbs.models.Product
import com.example.kvaldarbs.offerflow.currentuserID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.fragment_detail.*



class DetailFragment : Fragment() {
    lateinit var passedval: String
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passedval = arguments?.getString("entrytext").toString()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        keyref = database.child("products").child(passedval)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onStart() {
        super.onStart()

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<Product>()

                product?.let {
                    offerTitle.text = it.title
                    typeText.text = it.type
                    manufText.text = it.manufacturer
                    weightText.text = it.weight.toString()
                    deliveryText.text = it.delivery
                    //locationText.text = it.
                    wearText.text = it.wear
                    amountText.text = it.amount.toString()
                    descriptionText.text = it.description
                    Log.i(TAG, product.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.i(TAG, "loadPost:onCancelled" + error.toException().toString())
                Toast.makeText(requireContext(), "Failed to load post.", Toast.LENGTH_SHORT).show()
            }
        }

        keyref.addValueEventListener(valueEventListener)

//        // Keep copy of post listener so we can remove it when app stops
//        this.postListener = postListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val message = passedval
//        data.text = message




        val ree = PopUpDialog2Butt()
        val bundle = Bundle()
        ree.aaa = {


            navigateToConfirm()
        }

        offerButton.setOnClickListener {
            //onAlertDialog(view)

            database.child("products").child(passedval).child("isordered").setValue(true)

            val values: HashMap<String, Any> = HashMap()
            values[passedval] = true
            database.child("users").child(currentuserID).child("orders").updateChildren(values)

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