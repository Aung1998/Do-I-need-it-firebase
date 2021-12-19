package com.amt.doineedit_firebase.appDB

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Item(val itemName: String="", val price:Float= 0.0F, val quantity:Int=1, val haveItem:Boolean = false){

}

