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

interface CellClickListener {
    fun onCellClickListener(data: Product)
}


var productList = arrayListOf<Product>()

class FrontpageFragment : Fragment(), CellClickListener {

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "droidsays"

    lateinit var adapter: Adapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        database = Firebase.database.reference
        auth = Firebase.auth
        queryValueListener()



        val rootview = inflater.inflate(R.layout.fragment_frontpage, container, false)
        val recyclerView: RecyclerView = rootview.findViewById(R.id.recyclerview)

        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)
        adapter = Adapter(this.requireContext(), fetchList(), this)
        recyclerView.adapter = adapter

        // Inflate the layout for this fragment
        return rootview


    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }

    fun fetchList(): ArrayList<Product> {

    //        for (item in productList){
    //            Log.i(TAG, "fetchlist called")
    //            Log.i(TAG, item.title)
    //        }
        return productList

    //        val list = arrayListOf<RVData>()
    //
    //
    //        for (i in 0..20) {
    //            val model = RVData(R.drawable.furniturebackground, "Title : $i", "Subtitle : $i")
    //            list.add(model)
    //        }
    //        return list
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

    override fun onCellClickListener(data: Product) {
    //        Toast.makeText(this.requireContext(),data.title + " " + data.subtitle, Toast.LENGTH_SHORT).show()
        //val bundle = bundleOf("entrytext" to data.key)

        val intent = Intent(requireContext(), OrderHostActivity::class.java)
        intent.putExtra("key", data.key)
        startActivity(intent)
    }

    fun queryValueListener() {
        val allItemsQuery = database.child("products")

        allItemsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in dataSnapshot.children) {
                    Log.i(TAG, productSnapshot.toString())
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
                        //Log.i(TAG, productList.toString())
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

















