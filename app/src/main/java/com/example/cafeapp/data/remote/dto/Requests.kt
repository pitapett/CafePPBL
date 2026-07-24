package com.example.cafeapp.data.remote.dto
import com.google.gson.annotations.SerializedName
data class CreateOrderRequest(
    val tableId: String,
    val order_type: String = "Dine-in",
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val menuId: String,
    val quantity: Int,
    val price: Double,
    val tableId: String,
    val userId: String,
    val customization: String = ""
)

data class StockRequest(
    @SerializedName("ingredient_name")
    val ingredientName: String,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("unit")
    val unit: String // <-- Tambahkan field unit ini!
)

data class TableRequest(
    @SerializedName("table_number")
    val tableNumber: Int,

    @SerializedName("area")
    val area: String,

    @SerializedName("seat_count")
    val seatCount: Int, // ✅ Tambahkan jumlah kursi/kapasitas meja

    @SerializedName("status")
    val status: String? = "Available" // ✅ Default status jika diperlukan backend
)

data class TableStatusRequest(
    val status: String
)