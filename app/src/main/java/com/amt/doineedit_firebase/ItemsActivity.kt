package com.amt.doineedit_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    val ref = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        user = FirebaseAuth.getInstance().currentUser!!

        itemArrayList = ArrayList<Item>()
        val recyclerView = findViewById<RecyclerView>(R.id.rvItems)
        val recyclerViewAdapter = RecyclerViewAdapter(itemArrayList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        if (user != null){
            ref.child(("Items")).child(user!!.uid).addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val items = snapshot.getValue(Item::class.java)
                    itemArrayList.add(Item(items!!.itemName, items.price, items.quantity, items.haveItem))
                    recyclerViewAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }
    }

    fun addItem(v:View){
        ItemDialog(v.context, object : DialogListener{
            override fun onAddButtonClicked(item: Item){
                ref.child("Items").child(user!!.uid)
                    .push()
                    .setValue(item)
            }
        }).show()
    }

    private fun loadItem(){

    }
}