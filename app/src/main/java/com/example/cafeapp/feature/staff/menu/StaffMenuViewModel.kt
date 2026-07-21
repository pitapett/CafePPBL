package com.example.cafeapp.feature.staff.menu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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


// We use AndroidViewModel instead of ViewModel because we need the Application context to open the Room Database
class StaffMenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderRepository // <-- Change to OrderRepository

    init {
        val menuDao = CafeDatabase.Companion.getDatabase(application).menuDao()
        val draftCartDao = CafeDatabase.Companion.getDatabase(application).draftCartDao()
        repository = OrderRepository(
            RetrofitClient.api,
            menuDao,
            draftCartDao
        ) // <-- Change to OrderRepository
    }

    // Convert the Room Flow into a StateFlow so the UI can easily observe it
    val menuState = repository.getMenuStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = emptyList<MenuEntity>()
    )

    fun syncMenuWithServer() {
        viewModelScope.launch {
            repository.syncMenu()
        }
    }

    val liveCart = repository.getLiveCartStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addToCart(menuItem: MenuEntity) {
        // Database writes MUST happen on the background (IO) thread
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToCart(menuItem)
        }
    }

    fun decreaseFromCart(menuItem: MenuEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // Find the item in the current cart state
            val existingItem = liveCart.value.find { it.menuId == menuItem.id }

            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    // Use .copy() to safely mutate the data class
                    val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
                    repository.updateCartItem(updatedItem)
                } else {
                    // If quantity reaches 0, remove it entirely from the cart
                    repository.deleteCartItem(existingItem)
                }
            }
        }
    }

    private val _checkoutResult = MutableSharedFlow<Boolean>()
    val checkoutResult = _checkoutResult.asSharedFlow()

    // Change checkoutCart to accept the parameters
    fun checkoutCart(tableNumber: String, staffId: String) {
        viewModelScope.launch {
            val success = repository.processCheckout(tableNumber, staffId)
            _checkoutResult.emit(success)
        }
    }
}