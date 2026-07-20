package com.example.cafeapp.feature.staff.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.feature.staff.cart.CartDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDetailScreen(
    tableNumber: String,
    onBackClicked: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    viewModel: CartDetailViewModel = viewModel() // Inject your ViewModel
) {
    // Observe the Cart State from Room
    val cartItems by viewModel.liveCartState.collectAsState()

    // Local state to show the loading spinner on the button
    var isCheckingOut by remember { mutableStateOf(false) }

    // Dynamic calculations
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val tax = subtotal * 0.10 // Example: 10% tax. Remove if you don't use tax.
    val total = subtotal + tax

    // Listen for the successful network response
    LaunchedEffect(Unit) {
        viewModel.checkoutResult.collect { success ->
            isCheckingOut = false
            if (success) {
                onCheckoutSuccess()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Table $tableNumber Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Menu")
                    }
                }
            )
        },
        bottomBar = {
            // Only show the checkout bar if there are items in the cart
            if (cartItems.isNotEmpty()) {
                CheckoutBottomBar(
                    total = total,
                    isCheckingOut = isCheckingOut,
                    onCheckoutClicked = {
                        isCheckingOut = true
                        // Pass the actual logged-in Staff ID here
                        viewModel.checkoutCart(tableNumber, staffId = "STAFF_001")
                    }
                )
            }
        }
    ) { paddingValues ->

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("The cart is empty.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onIncreaseQty = { viewModel.updateQuantity(item, isIncrease = true) },
                        onDecreaseQty = { viewModel.updateQuantity(item, isIncrease = false) },
                        onRemoveClicked = { viewModel.removeItem(item) }
                    )
                }
            }
        }
    }
}

// 🧱 LOCAL COMPONENT: The individual item in the cart
@Composable
private fun CartItemCard(
    item: DraftCartEntity,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    // Show customization if it exists
                    if (!item.customization.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Note: ${item.customization}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
                Text(
                    text = "Rp ${item.price.toInt() * item.quantity}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete Button
                TextButton(
                    onClick = onRemoveClicked,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove")
                }

                // Plus/Minus Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecreaseQty) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncreaseQty) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        }
    }
}

// 🧱 LOCAL COMPONENT: The bottom sticky bar for totals
@Composable
private fun CheckoutBottomBar(
    total: Double,
    isCheckingOut: Boolean,
    onCheckoutClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = "Rp ${total.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckoutClicked,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isCheckingOut // Disables while processing
            ) {
                if (isCheckingOut) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirm Checkout")
                }
            }
        }
    }
}