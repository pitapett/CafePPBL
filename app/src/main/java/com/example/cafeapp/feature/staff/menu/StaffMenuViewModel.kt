package com.example.cafeapp.feature.staff.menu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.local.CafeDatabase
import com.example.cafeapp.data.local.entity.MenuEntity
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StaffMenuViewModel(
    application: Application
) : AndroidViewModel(application) {

    internal var repository: OrderRepository = OrderRepository(
        RetrofitClient.api,
        CafeDatabase.getDatabase(application).menuDao(),
        CafeDatabase.getDatabase(application).draftCartDao()
    )

    val menuState = repository.getMenuStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val liveCart = repository.getLiveCartStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun syncMenuWithServer() {
        viewModelScope.launch {
            repository.syncMenu()
        }
    }

    fun addToCart(menuItem: MenuEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToCart(menuItem)
        }
    }

    fun decreaseFromCart(menuItem: MenuEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingItem =
                liveCart.value.find {
                    it.menuId == menuItem.id
                }

            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    val updatedItem = existingItem.copy(
                        quantity = existingItem.quantity - 1
                    )

                    repository.updateCartItem(updatedItem)
                } else {
                    repository.deleteCartItem(existingItem)
                }
            }
        }
    }

    private val _checkoutResult =
        MutableSharedFlow<Boolean>()

    val checkoutResult =
        _checkoutResult.asSharedFlow()

    fun checkoutCart(tableNumber: String, staffId: String) {
        viewModelScope.launch {
            val success =
                repository.processCheckout(
                    tableNumber,
                    staffId
                )

            _checkoutResult.emit(success)
        }
    }
}

class StaffMenuViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StaffMenuViewModel::class.java)) {
            return StaffMenuViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}