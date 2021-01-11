package com.example.kvaldarbs.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.activity_orders.*



class OrdersActivity : AppCompatActivity(), ProductAdapter.CellClickListener {
    //log tag definition
    val TAG = "droidsays"

    //list that contains all user orders
    var userorders = arrayListOf<String>()
    //list of Product class objects that is passed to the recyclerview adapter
    var productList = arrayListOf<Product>()

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var adapter: ProductAdapter
    lateinit var currentuserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        getOrders()

        //recyclerview setup
        adapter = ProductAdapter(this, fetchList(), this)
        orderRV.adapter = adapter
        orderRV.layoutManager = GridLayoutManager(this, 2)

        setSupportActionBar(findViewById(R.id.order_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My orders"
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

        val intent = Intent(this@OrdersActivity, DetailActivity::class.java)
        intent.putExtra("key", passkey)
        intent.putExtra("type", "order")
        startActivity(intent)
    }

    //method returns list to recyclerview
    fun fetchList(): ArrayList<Product> {
        return productList
    }

    //receives list of product keys from database that have been ordered by the user
    fun getOrders() {
        val allItemsQuery = database.child("users").child(currentuserID).child("orders")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userorders.clear()
                for (productSnapshot in dataSnapshot.children) {
//                    Log.i(TAG, productSnapshot.toString())
                    userorders.add(productSnapshot.key.toString())
                }
                queryValueListener()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }

    //receives filtered list of products from database that have been ordered by the user
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
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }
}









