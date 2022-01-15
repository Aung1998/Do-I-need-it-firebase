package com.amt.doineedit_firebase

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.amt.doineedit_firebase.appDB.Item

class ItemDialog(context: Context, var dialogListener: DialogListener, var editItem: Item? = null) :
    AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_item)

        val nameItem: EditText? = findViewById(R.id.etItemName)
        val quantityItem: EditText? = findViewById(R.id.etItemQuantity)
        val priceItem: EditText? = findViewById(R.id.etItemPrice)
        val cbPurchased: CheckBox? = findViewById(R.id.checkBoxPurchased)
        val cbLocation: CheckBox? = findViewById(R.id.checkBoxLocated)

        val btnSave: Button? = findViewById(R.id.btn_item_save)
        val btnCancel: Button? = findViewById(R.id.btn_item_cancel)

        // If item is not null, have item detail in Dialog
        if (editItem != null) {
            nameItem!!.setText(editItem!!.itemName)
            quantityItem!!.setText(editItem!!.quantity.toString())
            priceItem!!.setText(editItem!!.price.toString())
            cbPurchased!!.isChecked = editItem!!.haveItem
        }

        btnCancel?.setOnClickListener {
            cancel()
        }

        btnSave?.setOnClickListener {
            val itemName: String = nameItem?.text.toString()
            val quantity: String = quantityItem?.text.toString()
            val price: String = priceItem?.text.toString()
            val purchased: Boolean = cbPurchased!!.isChecked
            val located: Boolean = cbLocation!!.isChecked

            if (itemName.isEmpty() || quantity.isEmpty() || price.isEmpty()) {
                Toast.makeText(context, "Please Fill All!", Toast.LENGTH_SHORT).show()
            } else {
                val item: Item = Item(
                    itemName = itemName, quantity = quantity.toInt(),
                    price = price.toDouble(), haveItem = purchased
                )

                dialogListener.onDoneButtonClicked(item)

                if (located) {
                    dialogListener.geoAdd("item key")
                }

                dismiss()
            }
        }

        btnCancel?.setOnClickListener { cancel() }

    }
}