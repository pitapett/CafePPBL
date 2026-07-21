package com.example.cafeapp.data.repository

import android.util.Log
import com.example.cafeapp.data.local.dao.DraftCartDao
import com.example.cafeapp.data.local.dao.MenuDao
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.data.local.entity.MenuEntity
import com.example.cafeapp.data.remote.CafeApiService
import com.example.cafeapp.data.remote.dto.CreateOrderRequest
import com.example.cafeapp.data.remote.dto.OrderItemRequest
import com.example.cafeapp.data.remote.dto.PaymentRequest
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val apiService: CafeApiService,
    private val menuDao: MenuDao,
    private val draftCartDao: DraftCartDao
) {

    // --- MENU LOGIC ---
    fun getMenuStream(): Flow<List<MenuEntity>> {
        return menuDao.getAllLocalMenus()
    }

    suspend fun syncMenu() {
        try {
            val response = apiService.getAllMenu()
            if (response.isSuccessful && response.body() != null) {
                val networkMenus = response.body()!!
                val menuEntities = networkMenus.map { networkItem ->
                    MenuEntity(
                        id = networkItem.id,
                        name = networkItem.name,
                        category = networkItem.category,
                        price = networkItem.price.toDouble(),
                        description = networkItem.description,

                        // Use the property names as they appear in the data class, not the JSON
                        drinkCategory = networkItem.drinkCategory,
                        image = networkItem.image
                    )
                }
                menuDao.insertMenus(menuEntities)
                Log.d("CafeSync", "Successfully saved to Room!")
            }
        } catch (e: Exception) {
            Log.e("CafeSync", "Network request completely failed: ${e.message}")
        }
    }

    // --- CART LOGIC ---
    fun getLiveCartStream(): Flow<List<DraftCartEntity>> {
        return draftCartDao.getLiveCart()
    }

    suspend fun addToCart(menuItem: MenuEntity) {
        val existingItem = draftCartDao.getCartItemByMenuId(menuItem.id)
        if (existingItem != null) {
            existingItem.quantity += 1
            draftCartDao.updateCartItem(existingItem)
            Log.d("CafeCart", "Updated quantity of ${menuItem.name} to ${existingItem.quantity}")
        } else {
            val newItem = DraftCartEntity(
                menuId = menuItem.id,
                name = menuItem.name,
                price = menuItem.price,
                quantity = 1,
                customization = ""
            )
            draftCartDao.insertNewCartItem(newItem)
            Log.d("CafeCart", "Added new item to cart: ${menuItem.name}")
        }
    }

    suspend fun updateCartItem(item: DraftCartEntity) = draftCartDao.updateCartItem(item)

    suspend fun deleteCartItem(item: DraftCartEntity) = draftCartDao.deleteCartItem(item)

    suspend fun clearCart() {
        draftCartDao.clearCart()
    }

    // --- CHECKOUT & ORDER LOGIC ---
    // In OrderRepository.kt
    // In OrderRepository.kt
    suspend fun processCheckout(tableNumber: String, staffId: String): Boolean {
        try {
            val localItems = draftCartDao.getCartItemsList()
            if (localItems.isEmpty()) return false

            // Map local cart items directly to the JSON Array format the server expects
            val networkPayload = localItems.map { item ->
                OrderItemRequest(
                    menuId = item.menuId,
                    quantity = item.quantity,
                    price = item.price,
                    tableId = tableNumber,
                    userId = staffId, // The dynamic staff ID is preserved here
                    customization = item.customization
                )
            }

            // Send the raw list directly to the API
            val response = apiService.createOrder(networkPayload)

            if (response.isSuccessful && response.body()?.success == true) {
                draftCartDao.clearCart()
                return true
            } else {
                Log.e("CafeCheckout", "Server rejected order: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("CafeCheckout", "Failed to reach server: ${e.message}")
        }
        return false
    }
    suspend fun getProcessOrders() = apiService.getProcessOrders()

    suspend fun processPayment(orderId: String, method: String) = apiService.payOrder(
        orderId,
        PaymentRequest(method)
    )
}