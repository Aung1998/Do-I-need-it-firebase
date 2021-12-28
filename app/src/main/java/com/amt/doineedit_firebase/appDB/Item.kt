package com.amt.doineedit_firebase.appDB

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Item(
    val itemName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val haveItem: Boolean = false
) {
    var showMenu = false // to validate Menu shown or not

    @Exclude
    fun toMap(): Map<String, Any?> {
        /* used to edit item on database
        * Return in Map Type which can be converted to JSON structure
        * which was used on Firebase Realtime Database*/
        return mapOf(
            "itemName" to itemName,
            "price" to price,
            "quantity" to quantity,
            "haveItem" to haveItem
        )
    }
}

