package com.amt.doineedit_firebase.appDB

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class ItemTest{
    private lateinit var item:Item

    @Before
    fun setUp() {
        item = Item("Test", 1.4, 5, false)
    }

    @Test
    fun getShowMenu() {
        assertFalse(item.showMenu)
    }

    @Test
    fun setShowMenu() {
        item.showMenu = true
        assertTrue(item.showMenu)
    }

    @Test
    fun toMap() {
        val result = item.toMap()
        assertEquals(mapOf(
            "itemName" to "Test",
            "price" to 1.4,
            "quantity" to 5,
            "haveItem" to false
        ), result)
    }

    @Test
    fun getItemName() {
        assertEquals("Test", item.itemName)
    }

    @Test
    fun getPrice() {
        assertEquals(1.4, item.price, item.price)
    }

    @Test
    fun getQuantity() {
        assertEquals(5, item.quantity)
    }

    @Test
    fun getHaveItem(){
        assertEquals(false, item.haveItem)
    }
}