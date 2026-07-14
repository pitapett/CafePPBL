package com.example.cafeapp.feature.admin.stock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.remote.dto.StockResponse
import com.example.cafeapp.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStockScreen(
    viewModel: ManageStockViewModel = viewModel()
) {
    val stockState by viewModel.stockList.collectAsState()

    // Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<StockResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchStock()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Manage Stock") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedItem = null // Null means "Add New"
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add New Stock")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (stockState) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Resource.Error -> Text(text = stockState.message ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is Resource.Success -> {
                    val stockItems = stockState.data ?: emptyList()
                    if (stockItems.isEmpty()) {
                        Text("No stock available.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(stockItems) { item ->
                                StockItemCard(
                                    item = item,
                                    onEditClicked = {
                                        selectedItem = item
                                        showDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is Resource.Idle -> {}
            }
        }
    }

    if (showDialog) {
        StockDialog(
            item = selectedItem,
            onDismiss = { showDialog = false },
            onSave = { name, amount ->
                if (selectedItem == null) {
                    viewModel.addStock(name, amount)
                } else {
                    viewModel.updateStock(selectedItem!!.id.toString(), name, amount)
                }
                showDialog = false
            },
            onDelete = {
                selectedItem?.let { viewModel.deleteStock(it.id.toString()) }
                showDialog = false
            }
        )
    }
}

@Composable
private fun StockItemCard(item: StockResponse, onEditClicked: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = item.ingredient_name ?: "Unknown", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Qty: ${item.amount}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

@Composable
private fun StockDialog(
    item: StockResponse?,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(item?.ingredient_name ?: "") }
    var amount by remember { mutableStateOf(item?.amount?.toString() ?: "") }
    val isEditing = item != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Stock" else "Add Stock") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = amount.toIntOrNull() ?: 0
                if (name.isNotBlank()) onSave(name, qty)
            }) { Text("Save") }
        },
        dismissButton = {
            Row {
                if (isEditing) {
                    TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete") }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}