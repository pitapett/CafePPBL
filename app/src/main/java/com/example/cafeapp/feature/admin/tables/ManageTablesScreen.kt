package com.example.cafeapp.feature.admin.tables

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.remote.dto.TableResponse
import com.example.cafeapp.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTablesScreen(
    viewModel: ManageTablesViewModel = viewModel()
) {
    val tableState by viewModel.tables.collectAsState()

    // Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<TableResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchTables()
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
            when (tableState) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Resource.Error -> Text(text = tableState.message ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is Resource.Success -> {
                    val tables = tableState.data ?: emptyList()
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
                is Resource.Idle -> { }
            }
        }
    }

    if (showDialog) {
        TableDialog(
            table = selectedItem,
            onDismiss = { showDialog = false },
            onSave = { number, area ->
                if (selectedItem == null) {
                    viewModel.addTable(number, area)
                } else {
                    viewModel.updateTable(selectedItem!!.id.toString(), number, area)
                }
                showDialog = false
            },
            onDelete = {
                selectedItem?.let { viewModel.deleteTable(it.id.toString()) }
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableGridCard(table: TableResponse, onClick: () -> Unit) {
    val statusColor = when (table.status?.uppercase()) {
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Table ${table.tableNumber}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Area: ${table.area ?: "Main"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Surface(color = statusColor.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
                Text(text = table.status ?: "UNKNOWN", color = statusColor, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun TableDialog(
    table: TableResponse?,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit,
    onDelete: () -> Unit
) {
    var tableNumber by remember { mutableStateOf(table?.tableNumber?.toString() ?: "") }
    var area by remember { mutableStateOf(table?.area ?: "") }
    val isEditing = table != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Table" else "Add Table") },
        text = {
            Column {
                OutlinedTextField(value = tableNumber, onValueChange = { tableNumber = it }, label = { Text("Table Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Area (e.g., Indoor, Patio)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val num = tableNumber.toIntOrNull() ?: 0
                if (num > 0) onSave(num, area)
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