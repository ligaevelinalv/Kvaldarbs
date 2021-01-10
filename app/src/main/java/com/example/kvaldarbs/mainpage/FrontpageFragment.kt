package com.example.kvaldarbs.mainpage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.models.Product
import com.example.kvaldarbs.orderflow.OrderHostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//cellclicklistener interface from the recyclerview adapter
interface CellClickListener {
    fun onCellClickListener(data: Product)
}

//list of Product class objects that is passed to the recyclerview adapter
var productList = arrayListOf<Product>()

class FrontpageFragment : Fragment(), CellClickListener {
    //log tag definition
    val TAG = "droidsays"

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    lateinit var adapter: Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth

        queryValueListener()

        //recyclerview setup
        val rootview = inflater.inflate(R.layout.fragment_frontpage, container, false)
        val recyclerView: RecyclerView = rootview.findViewById(R.id.recyclerview)

        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)
        adapter = Adapter(this.requireContext(), fetchList(), this)
        recyclerView.adapter = adapter

        (activity as MainScreen).supportActionBar?.title = "Frontpage"

        //inflate the layout for this fragment
        return rootview
    }

    //method returns list to recyclerview
    fun fetchList(): ArrayList<Product> {

        return productList
    }

    //recyclerview oncellclick listener, calls OrderHostActivity when user clicks on an item in the recyclerview
    override fun onCellClickListener(data: Product) {

        val intent = Intent(requireContext(), OrderHostActivity::class.java)
        intent.putExtra("key", data.key)
        startActivity(intent)
    }

    //receives list of products from database, refreshes data in recyclerview
    fun queryValueListener() {
        val allItemsQuery = database.child("products")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //clearing old data
                productList.clear()
                for (productSnapshot in dataSnapshot.children) {
                    //casts product from database if it has not been ordered and is visible
                    if ((productSnapshot.child("isordered").value.toString() == "false") && (productSnapshot.child("visible").value.toString() == "true")){
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
                            null,
                            true,
                            null
                        )
                        )
                    }
                }
                //notifies recyclerview about new data retrieval
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }
}

















