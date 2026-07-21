package com.example.cafeapp.data.remote

import com.example.cafeapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CafeApiService {

    // ==========================================
    // ORDER ROUTES
    // ==========================================

    @GET("order/menu")
    suspend fun getAllMenu(): Response<List<MenuResponse>>

    @POST("order/create")
    suspend fun createOrder(@Body orderItems: List<OrderItemRequest>): Response<OrderResponse>

    @GET("order/process")
    suspend fun getProcessOrders(): Response<List<ProcessOrderResponse>>

    @PUT("order/pay/{orderId}")
    suspend fun payOrder(
        @Path("orderId") orderId: String,
        @Body request: PaymentRequest
    ): Response<BaseResponse>

    // ==========================================
    // TABLE ROUTES
    // ==========================================

    @GET("tableInformation/all")
    suspend fun getAllTables(): Response<List<TableResponse>>

    @POST("tableInformation/create")
    suspend fun createTable(@Body request: TableRequest): Response<BaseResponse>

    @PUT("tableInformation/{id}")
    suspend fun updateTable(
        @Path("id") id: String,
        @Body request: TableRequest
    ): Response<BaseResponse>

    @PUT("tableInformation/{id}")
    suspend fun updateTableStatus(
        @Path("id") id: String,
        @Body request: TableStatusRequest
    ): Response<BaseResponse>

    @DELETE("tableInformation/{id}")
    suspend fun deleteTable(@Path("id") id: String): Response<BaseResponse>


    // ==========================================
    // STOCK ROUTES
    // ==========================================

    @GET("stock/all")
    suspend fun getAllStock(): Response<List<StockResponse>>

    @POST("stock/create")
    suspend fun createStock(@Body request: StockRequest): Response<StockResponse>

    @PUT("stock/update/{id}")
    suspend fun updateStock(
        @Path("id") id: String,
        @Body request: StockRequest
    ): Response<StockResponse>

    @DELETE("stock/delete/{id}")
    suspend fun deleteStock(@Path("id") id: String): Response<BaseResponse>
}