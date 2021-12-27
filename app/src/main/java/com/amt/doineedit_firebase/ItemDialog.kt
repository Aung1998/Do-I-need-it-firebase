package com.amt.doineedit_firebase
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.amt.doineedit_firebase.appDB.Item
import com.amt.doineedit_firebase.R

class ItemDialog(context: Context,var dialogListener: DialogListener, var editItem:Item? = null): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_item)

        Log.i("Dialog", "Dialog Created")

        val name_item:EditText? = findViewById(R.id.etItemName)
        val quantity_item:EditText? = findViewById(R.id.etItemQuantity)
        val price_item:EditText? = findViewById(R.id.etItemPrice)
        val cb_purchased:CheckBox? = findViewById(R.id.checkBoxPurchased)
        val cb_location:CheckBox? = findViewById(R.id.checkBoxLocated)

        val btn_save: Button? = findViewById(R.id.btn_item_save)
        val btn_cancel: Button? = findViewById(R.id.btn_item_cancel)

        if (editItem != null){
            name_item!!.setText(editItem!!.itemName)
            quantity_item!!.setText(editItem!!.quantity.toString())
            price_item!!.setText(editItem!!.price.toString())
            cb_purchased!!.isChecked = editItem!!.haveItem
        }

        btn_cancel?.setOnClickListener {
            cancel()
        }

        btn_save?.setOnClickListener {
            val itemName: String = name_item?.text.toString()
            val quantity: String = quantity_item?.text.toString()
            val price: String = price_item?.text.toString()
            val purchased: Boolean = cb_purchased!!.isChecked
            val located: Boolean = cb_location!!.isChecked

            //val email: String = sharedPreferences.getString("Logged_in_email", "null")!!

            if (itemName.isEmpty() || quantity.isEmpty() || price.isEmpty()){
                Toast.makeText(context,"Please Fill All!",Toast.LENGTH_SHORT).show()
            }
            else{
                val item : Item = Item(itemName = itemName, quantity = quantity.toInt(),
                    price = price.toDouble(), haveItem = purchased)

                dialogListener.onAddButtonClicked(item)

                if(located){
                    dialogListener.geoAdd("item key")
                }

                dismiss()
            }
        }

        btn_cancel?.setOnClickListener { cancel() }

    }
}