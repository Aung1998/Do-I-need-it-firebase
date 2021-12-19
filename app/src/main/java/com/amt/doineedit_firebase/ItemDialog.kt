package com.amt.doineedit_firebase
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.amt.doineedit_firebase.appDB.Item
import com.amt.doineedit_firebase.R

class ItemDialog(context: Context,var dialogListener: DialogListener): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_item)

        Log.i("Dialog", "Dialog Created")

        val name_item:EditText? = findViewById(R.id.etItemName)
        val quantity_item:EditText? = findViewById(R.id.etItemQuantity)
        val price_item:EditText? = findViewById(R.id.etItemPrice)

        val btn_save: Button? = findViewById(R.id.btn_item_save)
        val btn_cancel: Button? = findViewById(R.id.btn_item_cancel)

        btn_cancel?.setOnClickListener {
            cancel()
        }

        btn_save?.setOnClickListener {
            val itemName: String = name_item?.text.toString()
            val quantity: Int = quantity_item?.text.toString().toInt()
            val price: Float = price_item?.text.toString().toFloat()

            //val email: String = sharedPreferences.getString("Logged_in_email", "null")!!

            if (itemName.isEmpty()){
                Toast.makeText(context,"Please Enter Item Name",Toast.LENGTH_SHORT).show()
            }

            val item : Item = Item(itemName = itemName, quantity = quantity,
                price = price)

            dialogListener.onAddButtonClicked(item)

            dismiss()
        }

        btn_cancel?.setOnClickListener { cancel() }

    }
}