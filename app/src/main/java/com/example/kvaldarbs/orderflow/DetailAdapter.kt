package com.example.kvaldarbs.orderflow

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kvaldarbs.R

class DetailAdapter  (private val context: Context,
                      private val list: ArrayList<Uri?>) :
        RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.detailImageRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.detaillistitem,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        Glide.with(holder.itemView.context)
                .load(data)
                .centerCrop()
                .override(1200, 1200)
                .into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.count()
    }





}