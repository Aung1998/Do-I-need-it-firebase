package com.amt.doineedit_firebase.appDB

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Item(val itemName: String="", val price:Float= 0.0F, val quantity:Int=1, val haveItem:Boolean = false){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "itemName" to itemName,
            "price" to price,
            "quantity" to quantity,
            "haveItem" to haveItem
        )
    }
}

