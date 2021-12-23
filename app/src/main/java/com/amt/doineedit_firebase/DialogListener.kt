package com.amt.doineedit_firebase

import com.amt.doineedit_firebase.appDB.Item

interface DialogListener {
    fun onAddButtonClicked(item:Item)
    fun geoAdd(itemKey:String)
}