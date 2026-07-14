package com.example.cafeapp.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cafeapp.data.local.entity.MenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {

    // Fetch all cached menus.
    // UI will update automatically if the there is change in database
    @Query("SELECT * FROM menu_table ORDER BY category ASC")
    fun getAllLocalMenus(): Flow<List<MenuEntity>>

    // Insert new menus from the API.
    // if a menu item already exists, it will update it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenus(menus: List<MenuEntity>)

    // clear the table for a fresh sync
    @Query("DELETE FROM menu_table")
    suspend fun clearMenus()
}