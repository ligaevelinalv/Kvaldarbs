package com.example.kvaldarbs.mainpage

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.models.Product

//FrontpageFragment recyclerview adapter, takes in context, a list of Product class objects and an onclicklistener method
class Adapter(private val context: Context,
              private val list: ArrayList<Product>,
              private val cellClickListener: CellClickListener
        ) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    //viewholder class declares elements defined in the list item of the recyclerview
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleFieldRW)
        val manufacturer: TextView = view.findViewById(R.id.manufacturerFieldRW)
        val delivery: TextView = view.findViewById(R.id.deliveryFieldRW)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    //onbindviewholder populates the recyclerview with data received from the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.title.text = data.title
        holder.manufacturer.text = data.manufacturer
        holder.delivery.text = data.delivery

        //onclicklistener declaration
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(data)
        }
    }


}