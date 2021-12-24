package com.amt.doineedit_firebase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity.apply
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amt.doineedit_firebase.appDB.Item
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
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
    private var hasGps: Boolean = false
    private var hasNetwork:Boolean = false
    private var permission : Boolean = false
    val ref = FirebaseDatabase.getInstance().reference
    private var currentLocation: Location? = null
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        permission = isLocationPermissionGranted()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        val itemTouchHelper = createItemTouchListener()

        itemArrayList = ArrayList<Item>()
        itemIdList = ArrayList<String>()
        val recyclerView = findViewById<RecyclerView>(R.id.rvItems)
        recyclerViewAdapter = RecyclerViewAdapter(itemArrayList, itemIdList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        itemTouchHelper.attachToRecyclerView(recyclerView)

        ref.child(("Users")).child(user.uid).child("Items").addChildEventListener(object:ChildEventListener{
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

    @SuppressLint("MissingPermission")
    fun location() {
        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps = location
            }


            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (permission) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }

            // Choose current location the better between GPS Location and Network Location
            currentLocation = if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                locationByGps
            } else {
                locationByNetwork
            }
        }
    }

    fun logOut(v:View){
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun addItem(v:View){
        location()
        ItemDialog(v.context, object : DialogListener{
            lateinit var key: String
            override fun onAddButtonClicked(item: Item) {
                key = ref.child("Users")
                    .child(user.uid)
                    .child("Items")
                    .push().key!!
                ref.child("Users")
                    .child(user.uid)
                    .child("Items")
                    .child(key).setValue(item)
            }

            // Work only if location checkbox is checked
            override fun geoAdd(itemKey: String) {
                val geofire = GeoFire(ref.child("Users").child(user.uid).child("Item locations"))
                geofire
                    .setLocation(key,
                        currentLocation?.let { GeoLocation(it.latitude, it.longitude) })
            }
        }).show()
    }

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
                //Right
                if(direction == ItemTouchHelper.RIGHT) {
                    //Show Item if Menu is show else delete Item
                    if (recyclerViewAdapter.isMenuShown()){
                        recyclerViewAdapter.closeMenu()
                    }
                    else{
                        ref.child("Users").child(user.uid).child("Items")
                            .child(itemIdList[viewHolder.adapterPosition])
                            .removeValue()
                        }
                }
                //Left
                else{
                    recyclerViewAdapter.showMenu(viewHolder.adapterPosition);
                }
            }
        }
        return ItemTouchHelper(itemTouchCallback)
    }
}