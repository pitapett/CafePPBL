package com.example.cafeapp.data.repository

import com.example.cafeapp.data.remote.CafeApiService
import com.example.cafeapp.data.remote.dto.TableRequest
import com.example.cafeapp.data.remote.dto.TableStatusRequest

class TableRepository(private val apiService: CafeApiService) {
    suspend fun getAllTables() = apiService.getAllTables()
    suspend fun createTable(request: TableRequest) = apiService.createTable(request)
    suspend fun updateTable(id: String, request: TableRequest) = apiService.updateTable(id, request)
    suspend fun deleteTable(id: String) = apiService.deleteTable(id)

    suspend fun updateTableStatus(id: String, status: String) = apiService.updateTableStatus(id,
        TableStatusRequest(status)
    )
}