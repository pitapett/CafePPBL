package com.example.cafeapp.ui.staff

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cafeapp.data.remote.dto.ProcessOrderResponse
import com.example.cafeapp.utils.Resource
import com.example.cafeapp.viewmodel.ActiveOrdersViewModel

// Warna diambil persis dari item_active_order.xml (cafe_navy, cafe_pink dari colors.xml;
// hijau #4CAF50 & putih hardcoded sama seperti di XML asli)
private val CafeNavy = Color(0xFF021A54)
private val CafePink = Color(0xFFFF85BB)
private val CafeGreen = Color(0xFF4CAF50)
private val CafeWhite = Color(0xFFFFFFFF)

/**
 * Pengganti ActiveOrdersActivity lama (XML + RecyclerView + ActiveOrdersAdapter).
 * Pola mengikuti StaffTableStatusActivity: ComponentActivity + viewModels() + setContent.
 */
class ActiveOrdersActivity : ComponentActivity() {

    private val viewModel: ActiveOrdersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchActiveOrders()

        setContent {
            ActiveOrdersScreen(viewModel)
        }
    }
}

@Composable
fun ActiveOrdersScreen(viewModel: ActiveOrdersViewModel) {
    val context = LocalContext.current
    val ordersState by viewModel.orders.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentStatus.collectAsStateWithLifecycle()

    // Order yang dipilih untuk ditampilkan dialog pemilihan metode pembayaran.
    // Pengganti showPaymentDialog(orderId) lama.
    var selectedOrderId by remember { mutableStateOf<String?>(null) }

    // Menggantikan observeViewModel() -> launch { viewModel.paymentStatus.collect { ... } }
    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is Resource.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                viewModel.resetPaymentStatus()
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetPaymentStatus()
            }
            else -> Unit
        }
    }

    when (val state = ordersState) {
        is Resource.Success -> {
            ActiveOrdersList(
                orders = state.data ?: emptyList(),
                onPayClicked = { order -> selectedOrderId = order.id },
            )
        }

        is Resource.Error -> {
            // Menggantikan Toast.makeText untuk error fetch list
            LaunchedEffect(state.message) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
        }

        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> Unit // Resource.Idle
    }

    // Pengganti AlertDialog.Builder(...).setItems(methods) { ... } lama
    val orderIdForDialog = selectedOrderId
    if (orderIdForDialog != null) {
        PaymentMethodDialog(
            onDismiss = { selectedOrderId = null },
            onMethodSelected = { method ->
                viewModel.processPayment(orderIdForDialog, method)
                selectedOrderId = null
            },
        )
    }
}

@Composable
private fun PaymentMethodDialog(
    onDismiss: () -> Unit,
    onMethodSelected: (String) -> Unit,
) {
    val methods = listOf("Cash", "QRIS")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Payment Method", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                methods.forEach { method ->
                    Text(
                        text = method,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMethodSelected(method) }
                            .padding(vertical = 12.dp),
                        fontSize = 16.sp,
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

/**
 * Pengganti ActiveOrdersAdapter (RecyclerView.ListAdapter) lama.
 *
 * Tidak perlu DiffUtil manual -- LazyColumn dengan `key = { it.id }` sudah
 * menangani diffing/recomposition secara otomatis selama list-nya immutable
 * (data class baru tiap kali state berubah, bukan mutasi list yang sama).
 *
 * @param orders daftar order aktif yang ditampilkan, urutan sama seperti dikirim.
 * @param onPayClicked dipanggil saat tombol "Process Payment" ditekan untuk order tertentu.
 */
@Composable
fun ActiveOrdersList(
    orders: List<ProcessOrderResponse>,
    onPayClicked: (ProcessOrderResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(orders, key = { it.id }) { order ->
            ActiveOrderItem(
                order = order,
                onPayClicked = { onPayClicked(order) },
            )
        }
    }
}

@Composable
private fun ActiveOrderItem(
    order: ProcessOrderResponse,
    onPayClicked: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = CafeWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Table ${order.tableInformation?.table_number ?: "?"}",
                    color = CafeNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = order.order_type ?: "",
                    color = CafePink,
                    fontSize = 14.sp,
                )
            }

            Text(
                text = "Rp ${order.total_price}",
                color = CafeGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )

            // btnProcessPayment -- disembunyikan kalau status sudah "Paid",
            // sama seperti View.GONE di OrderViewHolder.bind() lama
            if (order.payment?.status != "Paid") {
                Button(
                    onClick = onPayClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafeGreen,
                        contentColor = CafeWhite,
                    ),
                ) {
                    Text("Process Payment")
                }
            }
        }
    }
}