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

class DetailActivity : AppCompatActivity() {
    var images = arrayListOf<Uri?>()
    val TAG = "droidsays"
    var userorders = arrayListOf<String>()


    lateinit var passedval: String
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

        productKey = intent.getStringExtra("key").toString()
        parentActivity = intent.getStringExtra("type").toString()
        orderStatus = intent.getStringExtra("orderstatus").toString()
        visibility = intent.getStringExtra("visibility").toString()
        admincritic = intent.getStringExtra("admincritic").toString()

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        keyref = productKey.let { database.child("products").child(it) }
        getImages()

        adapter = ProfileDetailAdapter(this, fetchList())
        detailImagesrv.adapter = adapter
        detailImagesrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        setSupportActionBar(findViewById(R.id.detail_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(parentActivity == "offer") {
            detailActivityButton.visibility = View.GONE

            if (orderStatus == "false") {

                val dialog = PopUpDialog2Butt()
                val bundle = Bundle()
                dialog.aaa = {

                    navigateToConfirm()
                }

                deleteOfferButton.setOnClickListener {
                    bundle.putInt("dialogtype", 3)
//                    bundle.putString("key", passedval)
                    dialog.arguments = bundle
                    dialog.show(supportFragmentManager, "")
                }

                val visibilityDialog = PopUpDialog2Butt()
                val visBundle = Bundle()
                visibilityDialog.aaa = {
                    changeVisibility()
                }

                visibilityButton.setOnClickListener {
                    visBundle.putInt("dialogtype", 4)
                    visBundle.putString("key", productKey)
                    visBundle.putBoolean("visibility", visibility.toBoolean())
                    visibilityDialog.arguments = visBundle
                    visibilityDialog.show(supportFragmentManager, "")
                }

                if ((admincritic == "null") || (admincritic == "")){
                    adminEditField.visibility = View.GONE
                    editAdminProductButton.visibility = View.GONE
                    visibilityChangeLabel.visibility = View.GONE
                } else {
                    visibilityButton.visibility = View.GONE
                    editProductButton.visibility = View.GONE
                    adminEditField.text = admincritic


                    editAdminProductButton.setOnClickListener {
                        val intent = Intent(this@DetailActivity, EditProductActivity::class.java)
                        intent.putExtra("key", productKey)
                        intent.putExtra("type", "editadmin")
                        startActivity(intent)
                    }
                }

                editProductButton.setOnClickListener {
                    val intent = Intent(this@DetailActivity, EditProductActivity::class.java)
                    intent.putExtra("key", productKey)
                    intent.putExtra("type", "edit")
                    startActivity(intent)
                }
            }
            else {
                editProductButton.visibility = View.GONE
                deleteOfferButton.visibility = View.GONE
                visibilityButton.visibility = View.GONE
                adminEditField.visibility = View.GONE
                editAdminProductButton.visibility = View.GONE
                visibilityChangeLabel.visibility = View.GONE
            }
        }
        else {

            detailActivityButton.setOnClickListener {
                Log.i(com.example.kvaldarbs.dialogs.TAG, "deleting offer")

                database.child("products").child(productKey).removeValue().addOnSuccessListener {
                    database.child("users").child(currentuserID).child("orders").child(productKey).removeValue()
                    finish()
                }.addOnFailureListener {
                    Log.i(TAG, it.toString())
                    Toast.makeText(this@DetailActivity, "Delivery confirmation failed, please check internet connection", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun changeVisibility() {
        finish()
    }

    fun navigateToConfirm(){
        database.child("products").child(productKey).removeValue().addOnSuccessListener {
            database.child("users").child(currentuserID).child("offers").child(productKey).removeValue()
            finish()
        }.addOnFailureListener {
            Log.i(TAG, it.toString())
            Toast.makeText(this@DetailActivity, "Offer deletion failed, check internet connection", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

    fun fetchList(): ArrayList<Uri?> {
        return images
    }

    fun getImages() {
        val imageQuery = keyref.child("images")
        val temp = ArrayList<Uri?>()


        imageQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (productSnapshot in dataSnapshot.children) {
//                    Log.i(TAG, "key: $productSnapshot")
//                    Log.i(TAG, "value: " + productSnapshot.value.toString())

                    temp.add(productSnapshot.value.toString().toUri())
                }
                images.clear()
                images.addAll(temp)


                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(
                    com.example.kvaldarbs.dialogs.TAG,
                    "query fetching error: " + error.toException().toString()
                )
            }
        })

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
                    //isordered = it.isordered

                    Log.i(com.example.kvaldarbs.dialogs.TAG, product.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.i(
                    com.example.kvaldarbs.dialogs.TAG,
                    "loadPost:onCancelled" + error.toException().toString()
                )
                //Toast.makeText(this, "Failed to load post.", Toast.LENGTH_SHORT).show()
            }
        }

        keyref.addListenerForSingleValueEvent(valueEventListener)
    }

//    fun makeDummyList(): ArrayList<Product> {
//        productList.clear()
//        productList.add(Product(
//                "aaa", "aaa", "aaa", "aaa", "aaa",
//                1 , "aaa", "aaa", false,
//                "aaa", 1, 1, 1, 1,"aaa",
//                "aaa", "aaa", 1, "aaa", "aaa"))
//        return productList
//    }

    fun hideFields(type: String) {
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
                        widthActivityDetailLabel, lengthActivityDetailLabel, materialActivityDetailLabel, colorActivityDetailLabel
                    )
                )
            }
        }
    }

    fun hideLabelBasedOnType(type: ArrayList<TextView>) {
        for (item in type) {
            item.visibility = View.GONE
        }
    }
}