// data/local/dao/DraftCartDao.kt
package com.example.cafeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cafeapp.data.local.entity.DraftCartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftCartDao {

    // 1. Get the live cart to display at the bottom of the screen later
    @Query("SELECT * FROM draft_cart_table")
    fun getLiveCart(): Flow<List<DraftCartEntity>>

    // 2. Check if a specific menu item is already in the cart
    @Query("SELECT * FROM draft_cart_table WHERE menuId = :menuId LIMIT 1")
    suspend fun getCartItemByMenuId(menuId: String): DraftCartEntity?

    // 3. Insert a brand new item into the cart
    @Insert
    suspend fun insertNewCartItem(item: DraftCartEntity)

    // 4. Update the quantity of an existing item
    @Update
    suspend fun updateCartItem(item: DraftCartEntity)

    // 5. Clear the cart after the order is successfully sent to the server
    @Query("DELETE FROM draft_cart_table")
    suspend fun clearCart()

    @Query("SELECT * FROM draft_cart_table")
    suspend fun getCartItemsList(): List<DraftCartEntity>


    @androidx.room.Delete
    suspend fun deleteCartItem(item: DraftCartEntity)
}