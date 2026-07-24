package com.example.cafeapp.feature.staff.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.local.CafeDatabase
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartDetailViewModel(
    application: Application,
    private val repository: OrderRepository = OrderRepository(
        RetrofitClient.api,
        CafeDatabase.getDatabase(application).menuDao(),
        CafeDatabase.getDatabase(application).draftCartDao()
    )
) : AndroidViewModel(application) {
    val liveCartState = repository.getLiveCartStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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