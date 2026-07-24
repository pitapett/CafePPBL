package com.example.cafeapp.feature.admin.tables

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.remote.dto.TableResponse
import com.example.cafeapp.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTablesScreen(
    viewModel: ManageTablesViewModel = viewModel()
) {
    val context = LocalContext.current
    val tableState by viewModel.tables.collectAsState()
    val actionStatus by viewModel.actionStatus.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<TableResponse?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchTables()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Toast feedback untuk action add/update/delete
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
        topBar = { TopAppBar(title = { Text("Manage Tables") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedItem = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add New Table")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = tableState) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Resource.Error -> Text(
                    text = state.message ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                is Resource.Success -> {
                    val tables = state.data ?: emptyList()
                    if (tables.isEmpty()) {
                        Text("No tables configured.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
                        ) {
                            items(tables) { table ->
                                TableGridCard(
                                    table = table,
                                    onClick = {
                                        selectedItem = table
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
        TableDialog(
            table = selectedItem,
            onDismiss = { showDialog = false },
            onSave = { number, area, seatCount ->
                if (selectedItem == null) {
                    viewModel.addTable(number, area, seatCount)
                } else {
                    viewModel.updateTable(selectedItem!!.id, number, area, seatCount)
                }
                showDialog = false
            },
            onDelete = {
                selectedItem?.let { viewModel.deleteTable(it.id) }
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableGridCard(table: TableResponse, onClick: () -> Unit) {
    val statusColor = when (table.status.uppercase()) {
        "AVAILABLE" -> Color(0xFF4CAF50)
        "OCCUPIED" -> Color(0xFFF44336)
        "CLEANING" -> Color(0xFFFF9800)
        "RESERVED" -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Table ${table.tableNumber}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Area: ${table.area}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Seats: ${table.seatCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = statusColor.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = table.status,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TableDialog(
    table: TableResponse?,
    onDismiss: () -> Unit,
    onSave: (Int, String, Int) -> Unit,
    onDelete: () -> Unit
) {
    var tableNumber by remember { mutableStateOf(table?.tableNumber?.toString() ?: "") }
    var area by remember { mutableStateOf(table?.area ?: "") }
    var seatCount by remember { mutableStateOf(table?.seatCount?.toString() ?: "4") } // Default 4 kursi
    val isEditing = table != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Table" else "Add Table") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { tableNumber = it },
                    label = { Text("Table Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area (e.g., Indoor, Outdoor)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = seatCount,
                    onValueChange = { seatCount = it },
                    label = { Text("Seat Count (Capacity)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val num = tableNumber.toIntOrNull() ?: 0
                val seats = seatCount.toIntOrNull() ?: 0
                if (num > 0 && area.isNotBlank() && seats > 0) {
                    onSave(num, area, seats)
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