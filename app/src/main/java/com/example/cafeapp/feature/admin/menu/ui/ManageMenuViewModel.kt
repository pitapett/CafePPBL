package com.example.cafeapp.feature.admin.menu.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.local.CafeDatabase
import com.example.cafeapp.data.local.entity.MenuEntity
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.repository.OrderRepository
import com.example.cafeapp.data.repository.TableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageMenuViewModel(
    private val application: Application,
    private val repository: OrderRepository
) : AndroidViewModel(application) {

    private val _menuState =
        MutableStateFlow<List<MenuEntity>>(emptyList())

    val menuState = _menuState.asStateFlow()

    fun loadMenu() {
        viewModelScope.launch {
            repository.syncMenu()

            repository.getMenuStream().collect {
                _menuState.value = it
            }
        }
    }
}