package com.example.cafeapp.feature.admin.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.remote.dto.StockRequest
import com.example.cafeapp.data.remote.dto.StockResponse
import com.example.cafeapp.data.repository.StockRepository
import com.example.cafeapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManageStockViewModel(
    internal var repository: StockRepository = StockRepository(RetrofitClient.api)
) : ViewModel() {

    private val _stockList = MutableStateFlow<Resource<List<StockResponse>>>(Resource.Idle())
    val stockList: StateFlow<Resource<List<StockResponse>>> = _stockList

    private val _actionStatus = MutableStateFlow<Resource<String>>(Resource.Idle())
    val actionStatus: StateFlow<Resource<String>> = _actionStatus

    fun fetchStock() {
        viewModelScope.launch {
            _stockList.value = Resource.Loading()
            try {
                val response = repository.getAllStock()
                if (response.isSuccessful && response.body() != null) {
                    _stockList.value = Resource.Success(response.body()!!.toList())
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to load stock"
                    _stockList.value = Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                _stockList.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun addStock(name: String, amount: Int, unit: String = "pcs") {
        viewModelScope.launch {
            _actionStatus.value = Resource.Loading()
            try {
                val request = StockRequest(
                    ingredientName = name,
                    amount = amount,
                    unit = unit
                )
                val response = repository.createStock(request)
                if (response.isSuccessful) {
                    _actionStatus.value = Resource.Success("Stock added successfully")
                    fetchStock()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to add stock"
                    _actionStatus.value = Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                _actionStatus.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun updateStock(id: String, name: String, amount: Int, unit: String = "pcs") {
        viewModelScope.launch {
            _actionStatus.value = Resource.Loading()
            try {
                val request = StockRequest(
                    ingredientName = name,
                    amount = amount,
                    unit = unit
                )
                val response = repository.updateStock(id, request)
                if (response.isSuccessful) {
                    _actionStatus.value = Resource.Success("Stock updated successfully")
                    fetchStock()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to update stock"
                    _actionStatus.value = Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                _actionStatus.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun deleteStock(id: String) {
        viewModelScope.launch {
            _actionStatus.value = Resource.Loading()
            try {
                val response = repository.deleteStock(id)
                if (response.isSuccessful) {
                    _actionStatus.value = Resource.Success("Stock deleted")
                    fetchStock()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to delete stock"
                    _actionStatus.value = Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                _actionStatus.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetActionStatus() {
        _actionStatus.value = Resource.Idle()
    }
}