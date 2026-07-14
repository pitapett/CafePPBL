package com.example.cafeapp.data.repository

import com.example.cafeapp.data.remote.CafeApiService
import com.example.cafeapp.data.remote.dto.StockRequest

class StockRepository(
    private val apiService: CafeApiService
) {
    suspend fun getAllStock() = apiService.getAllStock()

    suspend fun createStock(request: StockRequest) = apiService.createStock(request)

    suspend fun updateStock(id: String, request: StockRequest) = apiService.updateStock(id, request)

    suspend fun deleteStock(id: String) = apiService.deleteStock(id)
}