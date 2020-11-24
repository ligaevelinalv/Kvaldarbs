package com.example.kvaldarbs.mainpage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.models.RVData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface CellClickListener {
    fun onCellClickListener(data: RVData)
}

class FrontpageFragment : Fragment(), CellClickListener {

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "monitor"

    // Initializing an empty ArrayList to be filled with animals
    val dogs: ArrayList<String> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        database = Firebase.database.reference
        auth = Firebase.auth

        val rootview = inflater.inflate(R.layout.fragment_frontpage, container, false)
        val recyclerView: RecyclerView = rootview.findViewById(R.id.recyclerview)


        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.adapter = Adapter(this.requireContext(), fetchList(), this)

        // Inflate the layout for this fragment
        return rootview


    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }

    private fun fetchList(): ArrayList<RVData> {
        val list = arrayListOf<RVData>()

        for (i in 0..20) {
            val model = RVData(R.drawable.furniturebackground, "Title : $i", "Subtitle : $i")
            list.add(model)
        }
        return list
    }

    override fun onCellClickListener(data: RVData) {
//        Toast.makeText(this.requireContext(),data.title + " " + data.subtitle, Toast.LENGTH_SHORT).show()
        val bundle = bundleOf("entrytext" to data.title + " " + data.subtitle)
        findNavController().navigate(R.id.action_frontpageFragment_to_detailFragment, bundle)
    }
}