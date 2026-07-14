package com.example.cafeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.local.CafeDatabase
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CartDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderRepository // <-- Change to OrderRepository

    init {
        val menuDao = CafeDatabase.getDatabase(application).menuDao()
        val draftCartDao = CafeDatabase.getDatabase(application).draftCartDao()
        repository = OrderRepository(RetrofitClient.api, menuDao, draftCartDao) // <-- Change to OrderRepository
    }

    val liveCart = repository.getLiveCartStream()

    private val _checkoutResult = MutableSharedFlow<Boolean>()
    val checkoutResult = _checkoutResult.asSharedFlow()

    fun updateQuantity(item: DraftCartEntity, isIncrease: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isIncrease) {
                // DO NOT do item.quantity += 1. Use .copy() instead!
                val updatedItem = item.copy(quantity = item.quantity + 1)
                repository.updateCartItem(updatedItem)
            } else {
                if (item.quantity > 1) {
                    val updatedItem = item.copy(quantity = item.quantity - 1)
                    repository.updateCartItem(updatedItem)
                } else {
                    repository.deleteCartItem(item)
                }
            }
        }
    }

    fun updateCustomization(item: DraftCartEntity, newNote: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Also fixed this one to prevent text jumping bugs!
            val updatedItem = item.copy(customization = newNote)
            repository.updateCartItem(updatedItem)
        }
    }

    fun removeItem(item: DraftCartEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCartItem(item)
        }
    }

    fun checkoutCart(tableNumber: String, staffId: String) {
        viewModelScope.launch {
            val success = repository.processCheckout(tableNumber, staffId)
            _checkoutResult.emit(success)
        }
    }
}