package com.example.kvaldarbs.orderflow

import android.net.Uri
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.dialogs.TAG
import com.example.kvaldarbs.libs.utils.OfferViewModel
import com.example.kvaldarbs.libs.utils.OrderViewModel
import com.example.kvaldarbs.mainpage.productList
import com.example.kvaldarbs.models.Product
import com.example.kvaldarbs.offerflow.ImageAdapter
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

var imageList = arrayListOf<Uri?>()

class DetailFragment : Fragment() {
    lateinit var passedval: String
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference

    lateinit var adapter: DetailAdapter
    private val model: OrderViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        passedval = model.getValue()

        val rootview = inflater.inflate(R.layout.fragment_detail, container, false)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        keyref = database.child("products").child(passedval)

        val recyclerView: RecyclerView = rootview.findViewById(R.id.detailImagesRV)

        val placeholder = "android.resource://com.example.kvaldarbs/" + R.drawable.index
        imageList.add(placeholder.toUri())

        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = DetailAdapter(this.requireContext(), imageList)
        recyclerView.adapter = adapter

        // Inflate the layout for this fragment
        return rootview
    }

    override fun onStart() {
        super.onStart()

        val imageQuery = keyref.child("images")
        val temp = ArrayList<Uri?>()


        imageQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productSnapshot in dataSnapshot.children) {
//                    Log.i(TAG, "key: $productSnapshot")
//                    Log.i(TAG, "value: " + productSnapshot.value.toString())

                    temp.add(productSnapshot.value.toString().toUri())
                }
                imageList.clear()
                imageList.addAll(temp)


                adapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<Product>()

                product?.let {
                    offerTitle.text = it.title
                    typeText.text = it.type
                    manufText.text = it.manufacturer
                    deliveryText.text = it.delivery
                    wearText.text = it.wear
                    amountText.text = it.amount.toString()
                    locationText.text = it.location
                    descriptionText.text = it.description
                    weightText.text = it.weight.toString()
                    heightText.text = it.height.toString()
                    widthText.text = it.width.toString()
                    lengthText.text = it.length.toString()
                    materialText.text = it.material
                    colorText.text = it.color
                    authorText.text = it.author
                    yearText.text = it.year.toString()
                    bookTitleText.text = it.book_title
                    sizeText.text = it.size

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

            database.child("products").child(passedval).child("orderer").setValue(currentuserID)

            bundle.putInt("dialogtype", 1)
            ree.arguments = bundle
            ree.show(parentFragmentManager, "")

        }
    }

    fun navigateToConfirm(){

        findNavController().navigate(R.id.action_detailFragment_to_orderConfirmationFragment)

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