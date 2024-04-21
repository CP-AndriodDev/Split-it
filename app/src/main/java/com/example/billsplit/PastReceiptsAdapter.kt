package com.example.billsplit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PastReceiptsAdapter(private val receiptItems: List<ReceiptItem>) :
    RecyclerView.Adapter<PastReceiptsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tv_restaurant)
        val tvMyPortion: TextView = itemView.findViewById(R.id.tv_my_portion)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.past_receipts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val receiptItem = receiptItems[position]
        holder.tvRestaurantName.text = receiptItem.restaurantName
        holder.tvMyPortion.text = receiptItem.myPortion.toString()
        holder.tvDate.text = receiptItem.date
    }

    override fun getItemCount() = receiptItems.size
}