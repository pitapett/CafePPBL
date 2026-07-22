package com.example.cafeapp.feature.admin.menu.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.remote.dto.MenuResponse
import kotlinx.coroutines.launch

private const val IMAGE_BASE_URL = "http://10.0.2.2:3000/uploads/"

@Composable
fun ManageMenuScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: MenuViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var menus by remember { mutableStateOf<List<MenuResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State untuk delete
    var deletingId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedMenuId by remember { mutableStateOf<String?>(null) }
    var selectedMenuName by remember { mutableStateOf("") }

    // Fungsi load data (suspend)
    suspend fun loadData() {
        isLoading = true
        try {
            val response = RetrofitClient.api.getAllOfMenu()
            Log.d("MENU", "Code = ${response.code()}")
            if (response.isSuccessful) {
                menus = response.body() ?: emptyList()
                Log.d("MENU", "Jumlah = ${menus.size}")
            } else {
                Log.e("MENU", response.errorBody()?.string() ?: "")
                menus = emptyList()
            }
        } catch (e: Exception) {
            Log.e("MENU", e.toString())
            menus = emptyList()
        }
        isLoading = false
    }

    // Load data saat pertama kali
    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Text("+")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (menus.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No menu available")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(menus) { menu ->
                        val isDeleting = deletingId == menu.id

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = menu.image?.let { IMAGE_BASE_URL + it },
                                    contentDescription = menu.name,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = menu.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(menu.category)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Rp ${menu.price.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row {
                                        OutlinedButton(
                                            onClick = {
                                                onNavigateToEdit(menu.id)
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Edit")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                selectedMenuId = menu.id
                                                selectedMenuName = menu.name
                                                showDeleteDialog = true
                                            },
                                            enabled = !isDeleting,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            if (isDeleting) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    color = MaterialTheme.colorScheme.onError
                                                )
                                            } else {
                                                Icon(Icons.Default.Delete, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Delete")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog konfirmasi delete
    if (showDeleteDialog && selectedMenuId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Menu") },
            text = { Text("Apakah Anda yakin ingin menghapus \"$selectedMenuName\"?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        showDeleteDialog = false
                        val id = selectedMenuId ?: return@Button
                        deletingId = id

                        // Panggil delete di ViewModel
                        viewModel.deleteMenu(id) { success, message ->
                            deletingId = null
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                // Refresh data
                                scope.launch {
                                    loadData()
                                }
                            }
                        }
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}