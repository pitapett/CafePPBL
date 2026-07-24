package com.example.cafeapp.data.remote

import com.example.cafeapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.ResponseBody

interface CafeApiService {

    @Multipart
    @POST("menu/create")
    suspend fun createMenu(
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody,
        @Part("drink_category") drinkCategory: RequestBody?,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<MenuResponse>

    @Multipart
    @PUT("menu/update/{id}")
    suspend fun updateMenu(
        @Path("id") id: String,

        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody,
        @Part("drink_category") drinkCategory: RequestBody?,
        @Part("description") description: RequestBody,

        @Part image: MultipartBody.Part?
    ): Response<MenuResponse>

    // ==========================================
    // ORDER ROUTES
    // ==========================================

    @GET("menu/{id}")
    suspend fun getMenuById(
        @Path("id") id: String
    ): Response<MenuResponse>

    @DELETE("menu/delete/{id}")
    suspend fun deleteMenu(
        @Path("id") id: String
    ): Response<BaseResponse>

    @GET("menu/all")
    suspend fun getAllOfMenu(): Response<List<MenuResponse>>

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
    ): Response<ResponseBody> // <-- UBAH KELUARAN DARI StockResponse JADI ResponseBody

    @DELETE("stock/delete/{id}")
    suspend fun deleteStock(@Path("id") id: String): Response<BaseResponse>


}