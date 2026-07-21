package com.example.cafeapp.feature.staff.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.local.entity.MenuEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMenuScreen(
    tableNumber: String,
    onBackClicked: () -> Unit,
    onViewCartClicked: () -> Unit,
    viewModel: StaffMenuViewModel = viewModel()
) {
    val menuItems by viewModel.menuState.collectAsState()
    val cartItems by viewModel.liveCart.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.syncMenuWithServer()
    }

    val cartItemCount = cartItems.sumOf { it.quantity }
    val cartTotal = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Table $tableNumber Menu") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard")
                    }
                }
            )
        },
        floatingActionButton = {
            if (cartItemCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onViewCartClicked,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart") },
                    text = {
                        Text("View Cart ($cartItemCount) - Rp ${cartTotal.toInt()}")
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp)
        ) {
            items(menuItems) { item ->
                // Calculate the specific quantity for this item from the active cart state
                val cartQuantity = cartItems.find { it.menuId == item.id }?.quantity ?: 0

                MenuItemCard(
                    item = item,
                    cartQuantity = cartQuantity,
                    onAddClicked = { viewModel.addToCart(item) },
                    onDecreaseClicked = { viewModel.decreaseFromCart(item) }
                )
            }
        }
    }
}

// LOCAL COMPONENT
@Composable
private fun MenuItemCard(
    item: MenuEntity,
    cartQuantity: Int,
    onAddClicked: () -> Unit,
    onDecreaseClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                item.description.let { desc ->
                    Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(text = "Rp ${item.price.toInt()}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }

            // Conditionally render the buttons based on the current cart quantity
            if (cartQuantity > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDecreaseClicked,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease ${item.name} quantity")
                    }

                    Text(
                        text = cartQuantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = onAddClicked,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase ${item.name} quantity")
                    }
                }
            } else {
                IconButton(
                    onClick = onAddClicked,
                    modifier = Modifier.padding(start = 8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add ${item.name} to Cart")
                }
            }
        }
    }
}