package com.amt.doineedit_firebase

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item

class RecyclerViewAdapter (var itemArrayList: ArrayList<Item>):
    RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)
        return ItemHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = itemArrayList[position]
        holder.tvName.text = currentItem.itemName
        holder.tvPrice.text = "$${currentItem.price}"
        holder.tvQuantity.text = "${currentItem.quantity}"
        holder.cbPurchased.isChecked = currentItem.haveItem

        //holder.itemView.setOnClickListener()
    }

    override fun getItemCount(): Int {
        return itemArrayList.count()
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)!!
        val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)!!
        val tvQuantity = itemView.findViewById<TextView>(R.id.tvQuantity)!!
        val cbPurchased = itemView.findViewById<CheckBox>(R.id.cbPurchased)!!
    }


}