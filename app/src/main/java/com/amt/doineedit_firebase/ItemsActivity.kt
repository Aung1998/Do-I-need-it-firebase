package com.amt.doineedit_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity.apply
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class ItemsActivity : AppCompatActivity() {
    private lateinit var itemArrayList: ArrayList<Item>
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var itemIdList: ArrayList<String>
    val ref = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        val itemTouchHelper = createItemTouchListener()

        itemArrayList = ArrayList<Item>()
        itemIdList = ArrayList<String>()
        val recyclerView = findViewById<RecyclerView>(R.id.rvItems)
        val recyclerViewAdapter = RecyclerViewAdapter(itemArrayList, itemIdList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        itemTouchHelper.attachToRecyclerView(recyclerView)

        ref.child(("Items")).child(user.uid).addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue(Item::class.java)
                itemArrayList.add(Item(item!!.itemName, item.price, item.quantity, item.haveItem))
                itemIdList.add(snapshot.key.toString())
                recyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue(Item::class.java)
                if (item != null) {
                    for (i in 0 until itemArrayList.size) {
                        if (itemArrayList[i].toMap() != item.toMap()) {
                            itemArrayList[i] = item
                            break
                        }
                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Item::class.java)
                if (item != null) {
                    for (i in 0 until itemArrayList.size) {
                        if (itemArrayList[i].toMap() == item.toMap()) {
                            itemArrayList.removeAt(i)
                            itemIdList.removeAt(i)
                            recyclerViewAdapter.notifyItemRemoved(i)
                            break
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
               Log.i("Log Out", "logout successful!")
            }

        })
    }

    fun logOut(v:View){
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun addItem(v:View){
        ItemDialog(v.context, object : DialogListener{
            override fun onAddButtonClicked(item: Item){
                ref.child("Items").child(user.uid)
                    .push()
                    .setValue(item)
            }
        }).show()
    }

    private fun createItemTouchListener(): ItemTouchHelper {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if(direction == ItemTouchHelper.RIGHT) {
                    // remove data from database
                    ref.child("Items").child(user.uid)
                        .child(itemIdList[viewHolder.adapterPosition])
                        .removeValue()
                }
                else{

                    ItemDialog(viewHolder.itemView.context, object : DialogListener{
                        override fun onAddButtonClicked(item: Item){
                            ref.child("Items").child(user.uid)
                                .child(itemIdList[viewHolder.adapterPosition])
                                .updateChildren(item.toMap())
                        }
                    }).show()
                }
            }
        }
        return ItemTouchHelper(itemTouchCallback)
    }
}