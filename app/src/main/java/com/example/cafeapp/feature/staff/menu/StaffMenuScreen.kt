package com.example.cafeapp.feature.staff.menu

import android.app.Application
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cafeapp.data.local.entity.MenuEntity

private const val IMAGE_BASE_URL = "http://10.0.2.2:3000/uploads/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMenuScreen(
    tableNumber: String,
    onBackClicked: () -> Unit,
    onViewCartClicked: () -> Unit,
    context: Application = LocalContext.current.applicationContext as Application,
    viewModel: StaffMenuViewModel = viewModel(
        factory = StaffMenuViewModelFactory(context)
    )
) {
    LaunchedEffect(Unit) {
        viewModel.syncMenuWithServer()
    }

    val menuItems by viewModel.menuState.collectAsState()
    val cartItems by viewModel.liveCart.collectAsState()

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
                val cartQuantity = cartItems.find { it.menuId == item.id }?.quantity ?: 0

                MenuItemCard(
                    item = item,
                    onAddClicked = { viewModel.addToCart(item) }
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuEntity,
    onAddClicked: () -> Unit
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