package com.amt.doineedit_firebase.appDB

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Item(val itemName: String="", val price:Double= 0.0, val quantity:Int=1, val haveItem:Boolean = false){
    var showMenu = false
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

