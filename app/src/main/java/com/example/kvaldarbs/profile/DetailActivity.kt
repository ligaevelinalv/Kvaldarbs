package com.example.kvaldarbs.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.android.synthetic.main.dialog_critic.*
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailActivity : AppCompatActivity(){
    //log tag definition
    val TAG = "droidsays"

    //list of Uris that is passed to the recyclerview adapter
    var images = arrayListOf<Uri?>()

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference
    lateinit var currentuserID: String

    lateinit var productKey: String
    lateinit var adapter: ProfileDetailAdapter
    lateinit var parentActivity: String
    lateinit var orderStatus: String
    lateinit var visibility: String
    var admincritic: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //casting values passed through a bundle in navigation from Order or Offer Activity
        productKey = intent.getStringExtra("key").toString()
        parentActivity = intent.getStringExtra("type").toString()
        orderStatus = intent.getStringExtra("orderstatus").toString()
        visibility = intent.getStringExtra("visibility").toString()
        admincritic = intent.getStringExtra("admincritic").toString()

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        keyref = productKey.let { database.child("products").child(it) }
        getProductData()

        //recyclerview setup
        adapter = ProfileDetailAdapter(this, fetchList())
        detailImagesrv.adapter = adapter
        detailImagesrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        setSupportActionBar(findViewById(R.id.detail_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /**element visibility and functionality setup based on which activity called this activity**/

        //element visibility and functionality setup for displaying user offers
        if(parentActivity == "offer") {
            supportActionBar?.title = "Offer detail"

            detailActivityButton.visibility = View.GONE

            //element visibility and functionality setup for a product that has not been ordered yet
            if (orderStatus == "false") {

                //dialog setup
                val deletedialog = PopUpDialog2Butt()
                val bundle = Bundle()
                deletedialog.callback2butt = {

                    navigateToConfirm()
                }

                val visibilityDialog = PopUpDialog2Butt()
                val visBundle = Bundle()
                visibilityDialog.callback2butt = {
                    changeVisibility()
                }

                //button onclicklistener declaration
                deleteOfferButton.setOnClickListener {
                    bundle.putInt("dialogtype", 3)
                    deletedialog.arguments = bundle
                    deletedialog.show(supportFragmentManager, "")
                }

                visibilityButton.setOnClickListener {
                    visBundle.putInt("dialogtype", 4)
                    visBundle.putString("key", productKey)
                    visBundle.putBoolean("visibility", visibility.toBoolean())
                    visibilityDialog.arguments = visBundle
                    visibilityDialog.show(supportFragmentManager, "")
                }

                //element visibility and functionality setup for a product whose visibility
                // has not been changed by the administrator
                if ((admincritic == "null") || (admincritic == "")){
                    adminEditField.visibility = View.GONE
                    editAdminProductButton.visibility = View.GONE
                    visibilityChangeLabel.visibility = View.GONE

                    editProductButton.setOnClickListener {
                        val intent = Intent(this@DetailActivity, EditProductActivity::class.java)
                        intent.putExtra("key", productKey)
                        intent.putExtra("type", "edit")
                        startActivity(intent)
                    }

                //element visibility and functionality setup for a product whose visibility
                // has been changed by the administrator
                } else {
                    visibilityButton.visibility = View.GONE
                    editProductButton.visibility = View.GONE
                    adminEditField.text = admincritic

                    //edit
                    editAdminProductButton.setOnClickListener {
                        val intent = Intent(this@DetailActivity, EditProductActivity::class.java)
                        intent.putExtra("key", productKey)
                        intent.putExtra("type", "editadmin")
                        startActivity(intent)
                    }
                }
            }

            //element visibility and functionality setup for a product that has been ordered
            else {
                editProductButton.visibility = View.GONE
                deleteOfferButton.visibility = View.GONE
                visibilityButton.visibility = View.GONE
                adminEditField.visibility = View.GONE
                editAdminProductButton.visibility = View.GONE
                visibilityChangeLabel.visibility = View.GONE
            }
        }

        //element visibility and functionality setup for displaying user offers
        else {
            supportActionBar?.title = "Order detail"

            editProductButton.visibility = View.GONE
            deleteOfferButton.visibility = View.GONE
            visibilityButton.visibility = View.GONE
            adminEditField.visibility = View.GONE
            editAdminProductButton.visibility = View.GONE
            visibilityChangeLabel.visibility = View.GONE

            detailActivityButton.setOnClickListener {

                //deleting the product from user's list of orders (confirming delivery)
                database.child("products").child(productKey).removeValue()
                database.child("users").child(currentuserID).child("orders").child(productKey).removeValue()
                finish()

                Toast.makeText(this@DetailActivity, "Order delivery confirmed successfully.", Toast.LENGTH_LONG).show()
            }
        }
    }

    //callback that executes when user changes product visibility
    fun changeVisibility() {
        finish()

        Toast.makeText(this@DetailActivity, "Visibility changed successfully.", Toast.LENGTH_LONG).show()
    }

    //callback that executes when user deletes the product
    fun navigateToConfirm(){

        //deleting the product
        database.child("products").child(productKey).removeValue()
        database.child("users").child(currentuserID).child("offers").child(productKey).removeValue()
        finish()

        Toast.makeText(baseContext, "Offer deletion successful.", Toast.LENGTH_LONG).show()
    }

    //setup for back arrow navigation
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //menu setup
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

    //method returns list to recyclerview when the value in the viewmodel has changed
    fun fetchList(): ArrayList<Uri?> {
        return images
    }

    //query method for loading product data
    fun getProductData() {
        val imageQuery = keyref.child("images")
        val temp = ArrayList<Uri?>()

        //query for loading product images into the layout
        imageQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productSnapshot in dataSnapshot.children) {
                    temp.add(productSnapshot.value.toString().toUri())
                }
                //clearing old data
                images.clear()
                images.addAll(temp)

                //notifies recyclerview about new data retrieval
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG,"query fetching error: " + error.toException().toString()
                )
            }
        })

        //query for loading product data into the layout
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<Product>()
                product?.let {
                    hideFields(it.type)
                    offerDetailTitle.text = it.title
                    typeDetailText.text = it.type
                    manufDetailText.text = it.manufacturer
                    deliveryDetailText.text = it.delivery
                    wearDetailText.text = it.wear
                    amountDetailText.text = it.amount.toString()
                    locationDetailText.text = it.location
                    descriptionActivityDetailText.text = it.description
                    weightDetailText.text = it.weight.toString()
                    heightDetailText.text = it.height.toString()
                    widthDetailText.text = it.width.toString()
                    lengthDetailText.text = it.length.toString()
                    materialDetailText.text = it.material
                    colorDetailText.text = it.color
                    authorDetailText.text = it.author
                    yearDetailText.text = it.year.toString()
                    bookTitleDetailText.text = it.book_title
                    sizeDetailText.text = it.size

                    Log.i(TAG, product.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.i(TAG,"loadPost:onCancelled" + error.toException().toString()
                )
            }
        }

        keyref.addListenerForSingleValueEvent(valueEventListener)
    }

    fun hideFields(type: String) {

        //controlling visibility based on the product type
        when (type) {
            "Furniture" -> {
                hideLabelBasedOnType(
                    arrayListOf(
                        authorDetailText, yearDetailText, bookTitleDetailText, sizeDetailText,
                        authorActivityDetailLabel, yearActivityDetailLabel, bookTitleActivityDetailLabel, sizeActivityDetailLabel
                    )
                )
            }

            "Book" -> {
                hideLabelBasedOnType(
                    arrayListOf(
                        weightDetailText, heightDetailText, widthDetailText, lengthDetailText,
                        materialDetailText, colorDetailText, sizeDetailText, weightActivityDetailLabel, heightActivityDetailLabel,
                        widthActivityDetailLabel, lengthActivityDetailLabel, materialActivityDetailLabel, colorActivityDetailLabel,
                        sizeActivityDetailLabel
                    )
                )
            }

            "Decorations" -> {
                hideLabelBasedOnType(
                    arrayListOf(
                        weightDetailText, heightDetailText, widthDetailText, lengthDetailText,
                        authorDetailText, yearDetailText, bookTitleDetailText, weightActivityDetailLabel, heightActivityDetailLabel,
                        widthActivityDetailLabel, lengthActivityDetailLabel,
                            bookTitleActivityDetailLabel, authorActivityDetailLabel, yearActivityDetailLabel
                    )
                )
            }
        }
    }

    //visibility changing method
    fun hideLabelBasedOnType(type: ArrayList<TextView>) {
        for (item in type) {
            item.visibility = View.GONE
        }
    }

}