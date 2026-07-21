package com.example.cafeapp.data.remote.dto

import com.google.gson.annotations.SerializedName


data class TableResponse(
    val id: String,
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("seat_count") val seatCount: Int,
    val area: String,
    val status: String
)

data class MenuResponse(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    @SerializedName("drink_category") val drinkCategory: String?,
    val description: String,
    val drink_category: String?,
    val image: String?
)

data class BaseResponse(
    val message: String,
    val success: Boolean
)

data class OrderResponse(
    val success: Boolean,
    val message: String? = null,
    val data: OrderData? = null
)

data class OrderData(
    val id: String,
    val status: String,
    val total_price: Int
)

data class StockResponse(
    val id: String,
    val ingredient_name: String,
    val amount: Int
)


data class ProcessOrderResponse(
    val id: String,
    val order_type: String,
    val status: String,
    val total_price: Int,
    // This tells Retrofit to accept it whether Node.js sends it capitalized or not!
    @SerializedName("TableInformation", alternate = ["tableInformation", "table"])
    val tableInformation: TableInfoBrief?,

    @SerializedName("Payment", alternate = ["payment"])
    val payment: PaymentBrief?
)

data class TableInfoBrief(
    val id: String,
    val table_number: Int,
    val area: String
)

data class PaymentBrief(
    val id: String,
    val status: String,
    val method: String?
)

data class PaymentRequest(
    val method: String
)


