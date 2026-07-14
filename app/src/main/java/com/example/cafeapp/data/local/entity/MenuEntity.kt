package com.example.cafeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_table")
data class MenuEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String
)