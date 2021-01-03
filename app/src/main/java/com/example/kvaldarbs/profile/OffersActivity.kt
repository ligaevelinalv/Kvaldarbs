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

    val TAG = "droidsays"
    var userorders = arrayListOf<String>()
    var productList = arrayListOf<Product>()

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var adapter: ProductAdapter
    lateinit var currentuserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        getOffers()

        adapter = ProductAdapter(this, fetchList(), this)
        offerRV.adapter = adapter
        offerRV.layoutManager = GridLayoutManager(this, 2)


        setSupportActionBar(findViewById(R.id.offer_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainScreen::class.java))
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

    override fun onCellClickListener(data: Product) {

        val passkey =  data.key

        val intent = Intent(this@OffersActivity, DetailActivity::class.java)
        intent.putExtra("key", passkey)
        intent.putExtra("type", "offer")
        startActivity(intent)
    }

    fun fetchList(): ArrayList<Product> {
        return productList
    }

    fun getOffers() {
        val allItemsQuery = database.child("users").child(currentuserID).child("offers")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userorders.clear()
                for (productSnapshot in dataSnapshot.children) {
//                    Log.i(TAG, productSnapshot.toString())
                    userorders.add(productSnapshot.key.toString())
                }
                Log.i(TAG, userorders.toString())
                queryValueListener()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }

    fun queryValueListener() {
        productList.clear()

        val allItemsQuery = database.child("products")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (productSnapshot in dataSnapshot.children) {
                    if (userorders.contains(productSnapshot.key) ) {
                        Log.i(TAG, "filtered: $productSnapshot")
                        productList.add(Product(
                                productSnapshot.child("title").value.toString(),
                                productSnapshot.child("type").value.toString() ,
                                productSnapshot.child("manufacturer").value.toString(),
                                productSnapshot.child("delivery").value.toString(),
                                "",
                                0,
                                "",
                                "",
                                false,
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
                                null
                        ))
                    }

                }
                adapter.notifyDataSetChanged()
                //Log.i(TAG, userorders.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })

    }

    fun makeDummyList(): ArrayList<Product> {
        productList.clear()
        productList.add(Product(
                "aaa", "aaa", "aaa", "aaa", "aaa",
                1 , "aaa", "aaa", false,
                "aaa", 1, 1, 1, 1,"aaa",
                "aaa", "aaa", 1, "aaa", "aaa"))
        return productList
    }
}