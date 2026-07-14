package com.example.cafeapp.data.repository

import android.util.Log
import com.example.cafeapp.data.local.dao.DraftCartDao
import com.example.cafeapp.data.local.dao.MenuDao
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.data.local.entity.MenuEntity
import com.example.cafeapp.data.remote.CafeApiService
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
                        description = networkItem.description
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
    suspend fun processCheckout(tableNumber: String, staffId: String): Boolean {
        try {
            val localItems = draftCartDao.getCartItemsList()
            if (localItems.isEmpty()) return false

            val networkPayload = localItems.map { item ->
                OrderItemRequest(
                    menuId = item.menuId,
                    quantity = item.quantity,
                    price = item.price,
                    tableId = tableNumber,
                    userId = "2ef63ed0-aaed-4035-9970-86d344ce20e7", // hard coded userId that has staff role
                    customization = item.customization
                )
            }

            val response = apiService.createOrder(networkPayload)
            if (response.isSuccessful && response.body()?.success == true) {
                draftCartDao.clearCart()
                return true
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