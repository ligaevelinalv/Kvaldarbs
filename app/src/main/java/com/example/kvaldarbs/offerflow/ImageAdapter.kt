package com.example.kvaldarbs.offerflow

import android.content.Context
import android.media.Image
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.Adapter
import com.example.kvaldarbs.mainpage.CellClickListener
import com.example.kvaldarbs.models.Product

//MakeOfferFragment recyclerview adapter, takes in context and a list of image Uris
class ImageAdapter (private val context: Context,
                    private val list: ArrayList<Uri?>) :
        RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    //viewholder class declares elements defined in the list item of the recyclerview
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.finishedImageRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.imagelistitem,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    //onbindviewholder populates the recyclerview with images received from the list using the Glide library
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        Glide.with(holder.itemView.context)
            .load(data)
            .centerCrop()
            .override(400, 400)
            .into(holder.image)
    }


}