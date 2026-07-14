package com.example.cafeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_cart_table")
data class DraftCartEntity(
    @PrimaryKey(autoGenerate = true) val cartId: Int = 0,
    val menuId: String,
    val name: String,
    val price: Double,
    var quantity: Int,
    var customization: String = ""
)