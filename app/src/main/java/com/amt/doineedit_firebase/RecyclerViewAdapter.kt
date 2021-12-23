package com.amt.doineedit_firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.LocationCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RecyclerViewAdapter (var itemArrayList: ArrayList<Item>, var itemIDList:ArrayList<String>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SHOW_MENU = 1
    private val HIDE_MENU = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HIDE_MENU) {
            val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)
            ItemHolder(inflatedView)
        }else{
            val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.menu_adapter, parent, false)
            MenuHolder(inflatedView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemArrayList[position].showMenu){
            SHOW_MENU
        } else{
            HIDE_MENU
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemHolder){
            val currentItem = itemArrayList[position]
            holder.tvName.text = currentItem.itemName
            holder.tvPrice.text = "$${currentItem.price}"
            holder.tvQuantity.text = "${currentItem.quantity}"
            holder.cbPurchased.isChecked = currentItem.haveItem

            holder.cbPurchased.isEnabled = false

            holder.itemView.setOnClickListener{
                val item = itemArrayList[holder.adapterPosition]
                val message = "Item Name - ${item.itemName}\n " +
                        "Quantity - ${item.quantity}\n" +
                        "Price - ${item.price}" +
                        "Have bought? - ${if(item.haveItem) "yes" else "no"}"
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }
                Log.i("Height!", "${holder.itemView.height}")
                val shareIntent = Intent.createChooser(sendIntent, null)
                holder.itemView.context.startActivity(shareIntent)
            }
        }
        else if (holder is MenuHolder){
            val itemID = itemIDList[holder.adapterPosition]
            val user = FirebaseAuth.getInstance().currentUser
            val ref = FirebaseDatabase.getInstance().reference
            (holder as MenuHolder).btnEditText.setOnClickListener{
                ItemDialog(it.context, object : DialogListener{
                    override fun onAddButtonClicked(item: Item){
                        ref.child("Users").child(user!!.uid).child("Items")
                            .child(itemID)
                            .updateChildren(item.toMap())
                    }

                    override fun geoAdd(itemKey: String) {
                        // add location later
                    }
                }).show()
                notifyDataSetChanged()
            }
            (holder as MenuHolder).btnFindLocation.setOnClickListener{
                val geoFire = GeoFire(ref.child("Users").child(user!!.uid).child("Item locations"))
                val location =geoFire.getLocation(itemID, object: LocationCallback{
                    override fun onLocationResult(key: String?, location: GeoLocation?) {
                        if (location!=null){
                            val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}")
                            val intent = holder.showMap(gmmIntentUri)
                            val context = it.context
                            intent.setPackage("com.google.android.apps.maps")
                            context.startActivity(intent)
                        }
                        else{
                            Toast.makeText(it.context, "Location Not Found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    override fun getItemCount(): Int {
        return itemArrayList.count()
    }


    fun showMenu(position: Int) {
        for (i in 0 until itemArrayList.size) {
            itemArrayList[i].showMenu = false
        }
        itemArrayList[position].showMenu = true
        notifyDataSetChanged()
    }


    fun isMenuShown(): Boolean {
        for (i in 0 until itemArrayList.size) {
            if (itemArrayList[i].showMenu) {
                return true
            }
        }
        return false
    }

    fun closeMenu() {
        for (i in 0 until itemArrayList.size) {
            itemArrayList[i].showMenu = false
        }
        notifyDataSetChanged()
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)!!
        val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)!!
        val tvQuantity = itemView.findViewById<TextView>(R.id.tvQuantity)!!
        val cbPurchased = itemView.findViewById<CheckBox>(R.id.cbPurchased)!!
    }

    inner class MenuHolder(menuView: View) : RecyclerView.ViewHolder(menuView) {
        val btnEditText = menuView.findViewById<Button>(R.id.btnEditItem)
        val btnFindLocation = menuView.findViewById<Button>(R.id.btnFindLocation)
        fun showMap(geoLocation: Uri): Intent {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = geoLocation
            }
            return intent
        }

    }


}