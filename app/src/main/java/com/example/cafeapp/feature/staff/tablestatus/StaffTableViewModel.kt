package com.example.cafeapp.feature.staff.tablestatus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.remote.dto.TableResponse
import com.example.cafeapp.data.repository.TableRepository
import com.example.cafeapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StaffTableViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TableRepository(RetrofitClient.api)

    private val _tables = MutableStateFlow<Resource<List<TableResponse>>>(Resource.Idle())
    val tables: StateFlow<Resource<List<TableResponse>>> = _tables

    fun fetchTables() {
        viewModelScope.launch {
            _tables.value = Resource.Loading()
            try {
                val response = repository.getAllTables()
                if (response.isSuccessful && response.body() != null) {
                    _tables.value = Resource.Success(response.body()!!)
                } else {
                    _tables.value = Resource.Error("Failed to fetch tables")
                }
            } catch (e: Exception) {
                _tables.value = Resource.Error("Network error: ${e.message}")
            }
        }
    }

    fun toggleTableStatus(id: String, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                val newStatus =
                    if (isAvailable) "Available"
                    else "Unavailable"

                val response = repository.updateTableStatus(id, newStatus)

                if (response.isSuccessful) {
                    fetchTables()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}