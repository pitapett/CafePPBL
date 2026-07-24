package com.example.cafeapp.feature.staff.cart

import android.app.Application
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.local.entity.DraftCartEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDetailScreen(
    tableNumber: String,
    onBackClicked: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    // Tambahkan context & gunakan CartDetailViewModelFactory
    context: Application = LocalContext.current.applicationContext as Application,
    viewModel: CartDetailViewModel = viewModel(
        factory = CartDetailViewModelFactory(context)
    )
) {
    val context = LocalContext.current
    val cartItems by viewModel.liveCartState.collectAsState()
    var isCheckingOut by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.price * it.quantity }

    LaunchedEffect(Unit) {
        viewModel.checkoutResult.collect { success ->
            isCheckingOut = false
            if (success) {
                Toast.makeText(context, "Checkout successful", Toast.LENGTH_SHORT).show()
                onCheckoutSuccess()
            } else {
                Toast.makeText(context, "Checkout failed. Please try again.", Toast.LENGTH_LONG).show()
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
            if (cartItems.isNotEmpty()) {
                CheckoutBottomBar(
                    total = subtotal,
                    isCheckingOut = isCheckingOut,
                    onCheckoutClicked = {
                        isCheckingOut = true
                        viewModel.checkoutCart(tableNumber, staffId = "8c6f4079-3358-4341-afdc-f2e75398ddec")
                    }
                )
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
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
                items(cartItems, key = { it.cartId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncreaseQty = { viewModel.updateQuantity(item, isIncrease = true) },
                        onDecreaseQty = { viewModel.updateQuantity(item, isIncrease = false) },
                        onRemoveClicked = { viewModel.removeItem(item) },
                        onCustomizationChanged = { newNote ->
                            viewModel.updateCustomization(item, newNote)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: DraftCartEntity,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit,
    onRemoveClicked: () -> Unit,
    onCustomizationChanged: (String) -> Unit
) {
    var noteText by remember(item.customization) { mutableStateOf(item.customization) }

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
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Rp ${(item.price * item.quantity).toInt()}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = {
                    noteText = it
                    onCustomizationChanged(it)
                },
                label = { Text("Notes (e.g., Less ice, extra sugar)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onRemoveClicked,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove")
                }

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

@Composable
fun CheckoutBottomBar(
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
                enabled = !isCheckingOut
            ) {
                if (isCheckingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirm Checkout")
                }
            }
        }
    }
}