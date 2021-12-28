package com.amt.doineedit_firebase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var itemIdList: ArrayList<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val ref = FirebaseDatabase.getInstance().reference
    private var permission : Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        // Location permission
        permission = isLocationPermissionGranted()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // User
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        // Gesture Slide
        val itemTouchHelper = createItemTouchListener()

        itemArrayList = ArrayList<Item>()
        itemIdList = ArrayList<String>()
        val recyclerView = findViewById<RecyclerView>(R.id.rvItems)
        recyclerViewAdapter = RecyclerViewAdapter(itemArrayList, itemIdList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        // Add Slide  Gesture to RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)

        ref.child(("Users")).child(user.uid).child("Items").addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue(Item::class.java)
                itemArrayList.add(Item(item!!.itemName, item.price, item.quantity, item.haveItem))
                itemIdList.add(snapshot.key.toString())
                recyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // do nothing since item already changed from Recycler View Adapter
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
               Toast.makeText(baseContext, error.message, Toast.LENGTH_SHORT).show()
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
            lateinit var key: String

            override fun onDoneButtonClicked(item: Item) {
                key = ref.child("Users")
                    .child(user.uid)
                    .child("Items")
                    .push().key!!
                ref.child("Users")
                    .child(user.uid)
                    .child("Items")
                    .child(key).setValue(item)
            }

            // work only if the map app was opened before using Do-I-need-it.
            @SuppressLint("MissingPermission")
            override fun geoAdd(itemKey: String) {
                val geofire = GeoFire(ref.child("Users").child(user.uid).child("Item locations"))
                if (permission){
                    fusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
                        if(location != null){
                            geofire.setLocation(key, GeoLocation(location.latitude, location.longitude))
                        }
                        else{
                            Toast.makeText(v.context, "Location Not Found!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }).show()
    }

    // function that check if location permission have been granted and if not granted, ask for permission
    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            false
        } else {
            true
        }
    }

    // swipe function on ITem
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
                if(direction == ItemTouchHelper.RIGHT) {
                    //Show Item if Menu is show else delete Item
                    if (recyclerViewAdapter.isMenuShown()){
                        recyclerViewAdapter.closeMenu()
                    }
                    else{
                        ref.child("Users").child(user.uid).child("Items")
                            .child(itemIdList[viewHolder.adapterPosition])
                            .removeValue()

                        ref.child("Users").child(user.uid).child("Item locations")
                            .child(itemIdList[viewHolder.adapterPosition])
                            .removeValue()
                            .addOnFailureListener { /* In case of location doesn't exist */ }
                        }
                }
                else{
                    recyclerViewAdapter.showMenu(viewHolder.adapterPosition);
                }
            }
        }
        return ItemTouchHelper(itemTouchCallback)
    }
}