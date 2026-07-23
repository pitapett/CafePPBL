package com.example.cafeapp.feature.staff.orders

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

private val CafeNavy = Color(0xFF021A54)
private val CafePink = Color(0xFFFF85BB)
private val CafeGreen = Color(0xFF4CAF50)
private val CafeWhite = Color(0xFFFFFFFF)

@Composable
fun ActiveOrdersScreen(viewModel: ActiveOrdersViewModel) {
    val context = LocalContext.current
    val ordersState by viewModel.orders.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentStatus.collectAsStateWithLifecycle()

    var selectedOrderId by remember { mutableStateOf<String?>(null) }

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

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = ordersState) {
            is Resource.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is Resource.Error -> {
                Text(
                    text = state.message ?: "Failed to load orders",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is Resource.Success -> {
                val orders = state.data ?: emptyList()
                if (orders.isEmpty()) {
                    Text(
                        text = "No active orders.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    ActiveOrdersList(
                        orders = orders,
                        onPayClicked = { order -> selectedOrderId = order.id }
                    )
                }
            }
            else -> Unit
        }
    }

    val orderIdForDialog = selectedOrderId
    if (orderIdForDialog != null) {
        PaymentMethodDialog(
            onDismiss = { selectedOrderId = null },
            onMethodSelected = { method ->
                viewModel.processPayment(orderIdForDialog, method)
                selectedOrderId = null
            }
        )
    }
}

@Composable
fun PaymentMethodDialog(
    onDismiss: () -> Unit,
    onMethodSelected: (String) -> Unit
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
                        fontSize = 16.sp
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ActiveOrdersList(
    orders: List<ProcessOrderResponse>,
    onPayClicked: (ProcessOrderResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            ActiveOrderItem(
                order = order,
                onPayClicked = { onPayClicked(order) }
            )
        }
    }
}

@Composable
fun ActiveOrderItem(
    order: ProcessOrderResponse,
    onPayClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CafeWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Table ${order.tableInformation?.table_number ?: "?"}",
                    color = CafeNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = order.order_type,
                    color = CafePink,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "Rp ${order.total_price}",
                color = CafeGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (order.payment?.status != "Paid") {
                Button(
                    onClick = onPayClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafeGreen,
                        contentColor = CafeWhite
                    )
                ) {
                    Text("Process Payment")
                }
            }
        }
    }
}