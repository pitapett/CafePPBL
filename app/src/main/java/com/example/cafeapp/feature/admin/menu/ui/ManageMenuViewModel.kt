package com.example.cafeapp.feature.admin.menu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.local.entity.MenuEntity
import com.example.cafeapp.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageMenuViewModel(
    private val repository: OrderRepository
) : ViewModel() {

    private val _menuState = MutableStateFlow<List<MenuEntity>>(emptyList())
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