package com.example.kvaldarbs.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.CellClickListener
import com.example.kvaldarbs.models.Product

class ProductAdapter(private val context: Context,
                     private val list: ArrayList<Product>,
                     private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //val icon: ImageView = view.findViewById(R.id.icon)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
//        holder.icon.setImageDrawable(ContextCompat.getDrawable(context, data.icon))
        holder.title.text = data.title
        holder.manufacturer.text = data.manufacturer
        holder.delivery.text = data.delivery


        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(data)
        }
    }

    interface CellClickListener {
        fun onCellClickListener(data: Product)
    }
}






















