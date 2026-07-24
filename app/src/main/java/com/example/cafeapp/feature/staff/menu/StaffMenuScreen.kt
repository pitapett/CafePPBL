package com.example.cafeapp.feature.staff.menu

import androidx.compose.foundation.layout.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
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
import com.example.cafeapp.feature.staff.menu.StaffMenuViewModel
private const val IMAGE_BASE_URL = "http://10.0.2.2:3000/uploads/"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMenuScreen(
    tableNumber: String,
    onBackClicked: () -> Unit,
    onViewCartClicked: () -> Unit,
    viewModel: StaffMenuViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.syncMenuWithServer()
    }

    // Observe the states from your Room Database via the ViewModel
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

            AsyncImage(
                model = item.image?.let { IMAGE_BASE_URL + it },
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rp ${item.price.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onAddClicked
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add ${item.name}"
                )
            }
        }
    }
}