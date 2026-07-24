package com.example.cafeapp.feature.staff.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    application: Application
) : AndroidViewModel(application) {

    internal var repository: OrderRepository = OrderRepository(
        RetrofitClient.api,
        CafeDatabase.getDatabase(application).menuDao(),
        CafeDatabase.getDatabase(application).draftCartDao()
    )

    val liveCartState = repository.getLiveCartStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _checkoutResult =
        MutableSharedFlow<Boolean>()

    val checkoutResult =
        _checkoutResult.asSharedFlow()

    fun updateQuantity(
        item: DraftCartEntity,
        isIncrease: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            if (isIncrease) {
                val updatedItem = item.copy(quantity = item.quantity + 1)
                repository.updateCartItem(updatedItem)

            } else {

                if (item.quantity > 1) {

                    val updatedItem =
                        item.copy(
                            quantity = item.quantity - 1
                        )

                    repository.updateCartItem(updatedItem)

                } else {

                    repository.deleteCartItem(item)
                }
            }
        }
    }

    fun updateCustomization(
        item: DraftCartEntity,
        newNote: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedItem = item.copy(customization = newNote)
            repository.updateCartItem(updatedItem)
        }
    }

    fun removeItem(item: DraftCartEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCartItem(item)
        }
    }

    fun checkoutCart(
        tableNumber: String,
        staffId: String
    ) {
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

// Tambahkan Factory ini di bagian bawah file
class CartDetailViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartDetailViewModel::class.java)) {
            return CartDetailViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}