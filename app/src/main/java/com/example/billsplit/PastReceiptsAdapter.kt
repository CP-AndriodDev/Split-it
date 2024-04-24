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
        val tvBillTotal: TextView = itemView.findViewById(R.id.tv_bill_total)
        val tvTaxTotal: TextView = itemView.findViewById(R.id.tv_tax_total)
        val tvTipTotal: TextView = itemView.findViewById(R.id.tv_tip_total)
        val tvSplitAmount: TextView = itemView.findViewById(R.id.tv_split_amount)
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
        holder.tvBillTotal.text = receiptItem.billTotal.toString()
        holder.tvTaxTotal.text = receiptItem.taxTotal.toString()
        holder.tvTipTotal.text = receiptItem.tipPercentage.toString()
        holder.tvSplitAmount.text = receiptItem.splitAmount.toString()
        holder.tvMyPortion.text = receiptItem.myPortion.toString()
        holder.tvDate.text = receiptItem.date
    }

    override fun getItemCount() = receiptItems.size
}