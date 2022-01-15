package com.amt.doineedit_firebase

import com.amt.doineedit_firebase.appDB.Item

interface DialogListener {
    // used for both edit and add item
    fun onDoneButtonClicked(item: Item)

    // function to add location
    fun geoAdd(itemKey: String)
}