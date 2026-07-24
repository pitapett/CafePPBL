package com.example.cafeapp.feature.admin.stock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
    application: Application,
    private val repository: StockRepository = StockRepository(RetrofitClient.api)
) : AndroidViewModel(application) {
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
                    _stockList.value = Resource.Success(response.body()!!)
                } else {
                    _stockList.value = Resource.Error("Failed to load stock")
                }
            } catch (e: Exception) {
                _stockList.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun addStock(name: String, amount: Int) {
        viewModelScope.launch {
            _actionStatus.value = Resource.Loading()
            try {
                val response = repository.createStock(StockRequest(name, amount))
                if (response.isSuccessful) {
                    _actionStatus.value = Resource.Success("Stock added successfully")
                    fetchStock() // Refresh list
                } else {
                    _actionStatus.value = Resource.Error("Failed to add stock")
                }
            } catch (e: Exception) {
                _actionStatus.value = Resource.Error("Network error")
            }
        }
    }

    fun updateStock(id: String, name: String, amount: Int) {
        viewModelScope.launch {
            _actionStatus.value = Resource.Loading()
            try {
                val response = repository.updateStock(id, StockRequest(name, amount))
                if (response.isSuccessful) {
                    _actionStatus.value = Resource.Success("Stock updated successfully")
                    fetchStock() // Refresh list
                } else {
                    _actionStatus.value = Resource.Error("Failed to update stock")
                }
            } catch (e: Exception) {
                _actionStatus.value = Resource.Error("Network error")
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
                    fetchStock() // Refresh list
                } else {
                    _actionStatus.value = Resource.Error("Failed to delete stock")
                }
            } catch (e: Exception) {
                _actionStatus.value = Resource.Error("Network error")
            }
        }
    }

    fun resetActionStatus() {
        _actionStatus.value = Resource.Idle()
    }

}