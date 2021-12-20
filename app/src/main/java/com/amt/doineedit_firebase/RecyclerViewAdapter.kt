package com.amt.doineedit_firebase

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class RecyclerViewAdapter (var itemArrayList: ArrayList<Item>, var itemIDList:ArrayList<String>):
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

        holder.itemView.setOnClickListener{
            val user = FirebaseAuth.getInstance().currentUser!!
            val ref = FirebaseDatabase.getInstance().reference
            ItemDialog(holder.itemView.context, object : DialogListener{
                override fun onAddButtonClicked(item: Item){
                    ref.child("Items").child(user.uid).child(itemIDList[position])
                        .updateChildren(item.toMap())
                }
            }).show()
            Log.i("Item id", itemIDList[position])
        }
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