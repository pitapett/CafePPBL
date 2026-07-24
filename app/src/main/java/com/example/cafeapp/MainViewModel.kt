package com.example.cafeapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)

    private val _startRole = MutableStateFlow<String?>(null)
    val startRole: StateFlow<String?> = _startRole

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            repository.deviceRole
                .catch {
                    emit(null)
                }
                .collect { role ->
                    _startRole.value = role
                    _isLoading.value = false
                }
        }
    }

    fun saveRole(role: String) {
        viewModelScope.launch {
            repository.saveDeviceRole(role)
        }
    }

    fun clearRole() {
        viewModelScope.launch {
            repository.clearDeviceRole()
        }
    }
}