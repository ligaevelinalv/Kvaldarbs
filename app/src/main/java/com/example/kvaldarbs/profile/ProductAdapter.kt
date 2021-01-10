package com.example.kvaldarbs.profile

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.CellClickListener
import com.example.kvaldarbs.models.Product

//Orders and Offers Activity recyclerview adapter, takes in context, a list of Product class objects and an onclicklistener method
class ProductAdapter(private val context: Context,
                     private val list: ArrayList<Product>,
                     private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    //viewholder class declares elements defined in the list item of the recyclerview
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleFieldRW)
        val manufacturer: TextView = view.findViewById(R.id.manufacturerFieldRW)
        val delivery: TextView = view.findViewById(R.id.deliveryFieldRW)
        val cell: CardView = view.findViewById(R.id.cardView)
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
        //if the Product object admincritic variable is not empty, it will show up red in the
        // recyclerview to show that that an administrator has changed its visibility
        if (data.admincritic != ""){
            holder.cell.setCardBackgroundColor(Color.rgb(235, 176, 176))
        }

        holder.title.text = data.title
        holder.manufacturer.text = data.manufacturer
        holder.delivery.text = data.delivery

        //onclicklistener declaration
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(data)
        }

    }

    //interface for implementing method in Orders and Offers Activity
    interface CellClickListener {
        fun onCellClickListener(data: Product)
    }
}






















