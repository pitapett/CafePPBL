package com.example.cafeapp.feature.admin.stock

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.remote.dto.StockResponse
import com.example.cafeapp.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStockScreen(
    viewModel: ManageStockViewModel = viewModel()
) {
    val context = LocalContext.current
    val stockState by viewModel.stockList.collectAsState()
    val actionStatus by viewModel.actionStatus.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<StockResponse?>(null) }

    // Refresh data whenever the screen enters the RESUMED state
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchStock()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(actionStatus) {
        when (val status = actionStatus) {
            is Resource.Success -> {
                Toast.makeText(context, status.data ?: "Success", Toast.LENGTH_SHORT).show()
                viewModel.resetActionStatus()
            }
            is Resource.Error -> {
                Toast.makeText(context, status.message ?: "Error", Toast.LENGTH_LONG).show()
                viewModel.resetActionStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedItem = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Stock")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = stockState) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Resource.Error -> Text(
                    text = state.message ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                is Resource.Success -> {
                    val stockItems = state.data ?: emptyList()
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
            onSave = { name, amount, unit ->
                if (selectedItem == null) {
                    viewModel.addStock(name, amount, unit)
                } else {
                    viewModel.updateStock(selectedItem!!.id, name, amount, unit)
                }
                showDialog = false
            },
            onDelete = {
                selectedItem?.let { viewModel.deleteStock(it.id) }
                showDialog = false
            }
        )
    }
} // <-- Kurung tutup fungsi ManageStockScreen yang tadi hilang ada di sini!

@Composable
private fun StockItemCard(item: StockResponse, onEditClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = item.ingredientName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                // Menampilkan jumlah + unit (contoh: "Qty: 200 Bungkus")
                Text(
                    text = "Qty: ${item.amount} ${item.unit ?: ""}".trim(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

@Composable
fun StockDialog(
    item: StockResponse?,
    onDismiss: () -> Unit,
    onSave: (String, Int, String) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(item?.ingredientName ?: "") }
    var amount by remember { mutableStateOf(item?.amount?.toString() ?: "") }
    var unit by remember { mutableStateOf(item?.unit ?: "Bungkus") }
    val isEditing = item != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Stock" else "Add Stock") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (e.g. Bungkus, Kg, Pcs)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = amount.toIntOrNull() ?: 0
                if (name.isNotBlank() && unit.isNotBlank()) {
                    onSave(name, qty, unit)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (isEditing) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}