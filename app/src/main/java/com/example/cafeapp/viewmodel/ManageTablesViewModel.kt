package com.example.cafeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.remote.dto.TableRequest
import com.example.cafeapp.data.remote.dto.TableResponse
import com.example.cafeapp.data.repository.TableRepository
import com.example.cafeapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManageTablesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TableRepository(RetrofitClient.api)

    private val _tables = MutableStateFlow<Resource<List<TableResponse>>>(Resource.Idle())
    val tables: StateFlow<Resource<List<TableResponse>>> = _tables

    private val _actionStatus = MutableStateFlow<Resource<String>>(Resource.Idle())
    val actionStatus: StateFlow<Resource<String>> = _actionStatus

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

    fun addTable(tableNumber: Int, area: String) {
        executeTableAction("Added Table") {
            repository.createTable(TableRequest(tableNumber, area))
        }
    }

    fun updateTable(id: String, tableNumber: Int, area: String) {
        executeTableAction("Updated Table") {
            repository.updateTable(id, TableRequest(tableNumber, area))
        }
    }

    fun deleteTable(id: String) {
        executeTableAction("Deleted Table") {
            repository.deleteTable(id)
        }
    }

    // Abstracted helper to prevent boilerplate try/catch blocks
    private fun executeTableAction(successMessage: String, apiCall: suspend () -> retrofit2.Response<*>) {
        viewModelScope.launch {
            _actionStatus.value = Resource.Loading()
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    _actionStatus.value = Resource.Success(successMessage)
                    fetchTables() // Automatically refresh the list
                } else {
                    _actionStatus.value = Resource.Error("Operation failed")
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