package com.example.cafeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.local.CafeDatabase
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.remote.dto.ProcessOrderResponse
import com.example.cafeapp.data.repository.OrderRepository
import com.example.cafeapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActiveOrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OrderRepository // <-- Change to OrderRepository

    init {
        val menuDao = CafeDatabase.getDatabase(application).menuDao()
        val draftCartDao = CafeDatabase.getDatabase(application).draftCartDao()
        repository = OrderRepository(RetrofitClient.api, menuDao, draftCartDao) // <-- Change to OrderRepository
    }

    private val _orders = MutableStateFlow<Resource<List<ProcessOrderResponse>>>(Resource.Idle())
    val orders: StateFlow<Resource<List<ProcessOrderResponse>>> = _orders

    private val _paymentStatus = MutableStateFlow<Resource<String>>(Resource.Idle())
    val paymentStatus: StateFlow<Resource<String>> = _paymentStatus

    fun fetchActiveOrders() {
        viewModelScope.launch {
            _orders.value = Resource.Loading()
            try {
                val response = repository.getProcessOrders()
                if (response.isSuccessful && response.body() != null) {
                    _orders.value = Resource.Success(response.body()!!)
                } else {
                    _orders.value = Resource.Error("Failed to load orders")
                }
            } catch (e: Exception) {
                _orders.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun processPayment(orderId: String, method: String) {
        viewModelScope.launch {
            _paymentStatus.value = Resource.Loading()
            try {
                val response = repository.processPayment(orderId, method)
                if (response.isSuccessful) {
                    _paymentStatus.value = Resource.Success("Payment successful!")
                    fetchActiveOrders() // Refresh the list automatically
                } else {
                    _paymentStatus.value = Resource.Error("Payment failed")
                }
            } catch (e: Exception) {
                _paymentStatus.value = Resource.Error("Network error during payment")
            }
        }
    }

    fun resetPaymentStatus() {
        _paymentStatus.value = Resource.Idle()
    }
}