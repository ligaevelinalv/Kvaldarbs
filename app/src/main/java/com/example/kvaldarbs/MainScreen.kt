package com.example.kvaldarbs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.models.RVData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_mainscreen.*

interface CellClickListener {
    fun onCellClickListener(data: RVData)
}

class MainScreen: AppCompatActivity(), CellClickListener{
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "monitor"

    // Initializing an empty ArrayList to be filled with animals
    val dogs: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {

        database = Firebase.database.reference
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainscreen)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter(this, fetchList(), this)

    }

    private fun fetchList(): ArrayList<RVData> {
        val list = arrayListOf<RVData>()

        for (i in 0..20) {
            val model = RVData(R.drawable.furniturebackground, "Title : $i", "Subtitle : $i")
            list.add(model)
        }
        return list
    }

    override fun onCellClickListener(data: RVData) {
        Toast.makeText(this,data.title + " " + data.subtitle, Toast.LENGTH_SHORT).show()
    }
}