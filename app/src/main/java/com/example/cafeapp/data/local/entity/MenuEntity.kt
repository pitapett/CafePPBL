package com.example.cafeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// data/local/entity/MenuEntity.kt
@Entity(tableName = "menu_table")
data class MenuEntity(
    @PrimaryKey val id: String, // Pastikan tipe ini sama dengan response ID dari API
    val name: String,
    val price: Double,
    val category: String,
    val drinkCategory: String?, // Tambahkan ini
    val description: String,
    val image: String? // Tambahkan ini (nullable jika menu mungkin belum punya gambar)
)