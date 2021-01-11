package com.example.kvaldarbs.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen
import com.example.kvaldarbs.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_offers.*

class OffersActivity : AppCompatActivity(), ProductAdapter.CellClickListener {
    //log tag definition
    val TAG = "droidsays"

    //list that contains all user offers
    var useroffers = arrayListOf<String>()
    //list of Product class objects that is passed to the recyclerview adapter
    var productList = arrayListOf<Product>()

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var adapter: ProductAdapter
    lateinit var currentuserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        getOffers()

        //recyclerview setup
        adapter = ProductAdapter(this, fetchList(), this)
        offerRV.adapter = adapter
        offerRV.layoutManager = GridLayoutManager(this, 2)

        setSupportActionBar(findViewById(R.id.offer_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My offers"
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

    //recyclerview oncellclick listener, calls DetailActivity when user clicks on an item in the recyclerview
    override fun onCellClickListener(data: Product) {

        val passkey =  data.key
        val isordered = data.isordered.toString()
        val visibility = data.visible.toString()
        val admincritic = data.admincritic

        val intent = Intent(this@OffersActivity, DetailActivity::class.java)
        intent.putExtra("key", passkey)
        intent.putExtra("type", "offer")
        intent.putExtra("orderstatus", isordered)
        intent.putExtra("visibility", visibility)
        intent.putExtra("admincritic", admincritic)
        startActivity(intent)
    }

    //method returns list to recyclerview
    fun fetchList(): ArrayList<Product> {
        return productList
    }

    //receives list of product keys from database that have been offered by the user
    fun getOffers() {
        val allItemsQuery = database.child("users").child(currentuserID).child("offers")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                useroffers.clear()
                for (productSnapshot in dataSnapshot.children) {
                    useroffers.add(productSnapshot.key.toString())
                }
                queryValueListener()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }

    //receives filtered list of products from database that have been offered by the user
    fun queryValueListener() {

        val allItemsQuery = database.child("products")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in dataSnapshot.children) {
                    if (useroffers.contains(productSnapshot.key) ) {
                        productList.add(Product(
                            productSnapshot.child("title").value.toString(),
                            productSnapshot.child("type").value.toString() ,
                            productSnapshot.child("manufacturer").value.toString(),
                            productSnapshot.child("delivery").value.toString(),
                            "",
                            0,
                            "",
                            "",
                            productSnapshot.child("isordered").value.toString().toBoolean(),
                            productSnapshot.key.toString(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            productSnapshot.child("visible").value.toString().toBoolean(),
                            productSnapshot.child("admincritic").value.toString()
                        ))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }
}