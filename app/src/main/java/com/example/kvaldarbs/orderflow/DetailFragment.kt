package com.example.kvaldarbs.orderflow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.CriticDialog
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.libs.utils.OrderViewModel
import com.example.kvaldarbs.mainpage.MainScreen
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
    //log tag definition
    var TAG: String = "droidsays"

    //database variable declaration
    lateinit var passedval: String
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference
    lateinit var roleref: DatabaseReference
    //var isordered: Boolean? = false

    //list of Uris that is passed to the recyclerview adapter
    var imageList = arrayListOf<Uri?>()
    lateinit var adapter: DetailAdapter
    private val model: OrderViewModel by activityViewModels()
    lateinit var role: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //retrieving the key value of the product the user clicked on in FrontpageFragment recyclerview
        passedval = model.getValue()

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        keyref = database.child("products").child(passedval)
        roleref = database.child("users").child(currentuserID).child("role")


        //query to find what role the user has
        val roleQuery = roleref
        roleQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                role = dataSnapshot.value.toString()
                //chaning the visibility of specific elements based on the role
                if (role == "Administrator"){
                    orderButton.visibility = View.GONE

                } else {
                    changeVisibilityButton.visibility = View.GONE
                    deleteAdminButton.visibility = View.GONE
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(TAG, "role loading failed "+ databaseError.toException())
            }
        })

        //inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_detail, container, false)

        //placeholder image in recyclerview for slow connection
        val placeholder = "android.resource://com.example.kvaldarbs/" + R.drawable.index
        imageList.add(placeholder.toUri())

        //recyclerview setup
        val recyclerView: RecyclerView = rootview.findViewById(R.id.detailImagesRV)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = DetailAdapter(this.requireContext(), imageList)
        recyclerView.adapter = adapter

        val imageQuery = keyref.child("images")
        val temp = ArrayList<Uri?>()

        //query for loading product images
        imageQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (productSnapshot in dataSnapshot.children) {
                    temp.add(productSnapshot.value.toString().toUri())
                }

                //clearing old data
                imageList.clear()
                imageList.addAll(temp)

                //notifies recyclerview about new data retrieval
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })

        //query for loading product data into the layout
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<Product>()
                product?.let {
                    hideFields(it.type)
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
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "loadPost:onCancelled" + error.toException().toString())
                Toast.makeText(requireContext(), "Failed to load post.", Toast.LENGTH_SHORT).show()
            }
        }
        keyref.addListenerForSingleValueEvent(valueEventListener)
        return rootview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //dialog setup
        val orderdialog = PopUpDialog2Butt()
        val bundle = Bundle()
        orderdialog.callback2butt = {
            navigateToConfirm()
        }

        val criticdialog = CriticDialog()
        val criticbundle = Bundle()
        criticdialog.callbackcritic = {
            criticNavigateToConfirm()
        }

        val deleteDialog = PopUpDialog2Butt()
        val deleteBundle = Bundle()
        deleteDialog.callback2butt = {
            deleteNavigateToConfirm()
        }

        //button onclicklistener declaration
        orderButton.setOnClickListener {
            bundle.putInt("dialogtype", 1)
            orderdialog.arguments = bundle
            orderdialog.show(parentFragmentManager, "")
        }

        changeVisibilityButton.setOnClickListener {
            criticbundle.putInt("dialogtype", 1)
            criticbundle.putString("key", passedval)
            criticdialog.arguments = criticbundle
            criticdialog.show(parentFragmentManager, "")
        }

        deleteAdminButton.setOnClickListener {
            deleteBundle.putInt("dialogtype", 3)
            deleteDialog.arguments = deleteBundle
            deleteDialog.show(parentFragmentManager, "")
        }
    }

    //callback that executes when user orders the product
    fun navigateToConfirm(){

        findNavController().navigate(R.id.action_detailFragment_to_orderConfirmationFragment)

        //setting the product as ordered, adding it to the user order list
        database.child("products").child(passedval).child("isordered").setValue(true)
        val values: HashMap<String, Any> = HashMap()
        values[passedval] = true
        database.child("users").child(currentuserID).child("orders").updateChildren(values)
        database.child("products").child(passedval).child("orderer").setValue(currentuserID)
    }

    //callback that executes when administrator changes product visibility
    fun criticNavigateToConfirm() {

        val intent = Intent(requireContext(), MainScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        Toast.makeText(requireContext(), "Visibility changed successfully.", Toast.LENGTH_LONG).show()
    }

    //callback that executes when administrator deletes product
    fun deleteNavigateToConfirm() {
        findNavController().navigate(R.id.action_detailFragment_to_nav_graph)
        database.child("products").child(passedval).removeValue()
        database.child("users").child(currentuserID).child("offers").child(passedval).removeValue()

        Toast.makeText(requireContext(), "Offer deletion successful.", Toast.LENGTH_LONG).show()

    }

    fun hideFields(type: String?) {

        //controlling visibility based on the product type
        when (type) {
            "Furniture" -> {
                hideLabelBasedOnType(arrayListOf(authorText, yearText, bookTitleText, sizeText,
                        authorDetailLabel, yearDetailLabel, bookTitleDetailLabel, sizeDetailLAbel))
            }
            "Book" -> {
                hideLabelBasedOnType(arrayListOf(weightText, heightText, widthText, lengthText,
                        materialText, colorText, sizeText, weightDetailLabel, heightDetailLabel,
                        widthDetailLabel, lengthDetailLabel, materialDetailLabel, colorDetailLabel,
                        sizeDetailLAbel ))
            }
            "Decorations" -> {
                hideLabelBasedOnType(arrayListOf(weightText, heightText, widthText, lengthText,
                        authorText, yearText, bookTitleText, weightDetailLabel, heightDetailLabel,
                        widthDetailLabel, lengthDetailLabel,  authorDetailLabel, yearDetailLabel,
                        bookTitleDetailLabel))
            }
        }
    }

    //visibility changing method
    fun hideLabelBasedOnType(type: ArrayList<TextView>?) {
        if (type != null) {
            for (item in type) {
                item.visibility = View.GONE
            }
        }
    }
}