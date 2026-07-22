package com.example.cafeapp.feature.admin.menu.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

private const val IMAGE_BASE_URL = "http://10.0.2.2:3000/uploads/"

private val CATEGORY_OPTIONS = listOf("Main", "Appetizer", "Side", "Dessert", "Drink")
private val DRINK_CATEGORY_OPTIONS = listOf("Coffee", "Non-Coffee", "Tea", "Frappe", "Juice", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenuScreen(
    menuId: String,
    onNavigateBack: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val context = LocalContext.current

    val menu by viewModel.menu.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORY_OPTIONS.first()) }
    var drinkCategory by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var drinkCategoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(menuId) {
        viewModel.getMenuById(menuId)
    }

    LaunchedEffect(menu) {
        menu?.let {
            name = it.name
            price = it.price.toString()
            description = it.description ?: ""
            category = it.category
            drinkCategory = it.drinkCategory
            currentImageUrl = it.image
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    var isUpdating by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.getMenuById(menuId) }) {
                        Text("Coba lagi")
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Gambar
                    val displayImage = imageUri?.toString() ?: currentImageUrl?.let { IMAGE_BASE_URL + it }
                    if (displayImage != null) {
                        AsyncImage(
                            model = displayImage,
                            contentDescription = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tidak ada gambar")
                        }
                    }

                    Button(
                        onClick = {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (imageUri == null) "Pilih Gambar" else "Ganti Gambar")
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Menu") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Harga") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // ========== CATEGORY DROPDOWN ==========
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            CATEGORY_OPTIONS.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        category = option
                                        categoryExpanded = false
                                        if (option != "Drink") {
                                            drinkCategory = null
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // ========== DRINK CATEGORY DROPDOWN ==========
                    if (category == "Drink") {
                        ExposedDropdownMenuBox(
                            expanded = drinkCategoryExpanded,
                            onExpandedChange = { drinkCategoryExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = drinkCategory ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori Minuman") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = drinkCategoryExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = drinkCategoryExpanded,
                                onDismissRequest = { drinkCategoryExpanded = false }
                            ) {
                                DRINK_CATEGORY_OPTIONS.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            drinkCategory = option
                                            drinkCategoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = "—",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori Minuman (hanya untuk Drink)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        )
                    }

                    // ========== UPDATE BUTTON ==========
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        onClick = {
                            val priceInt = price.toIntOrNull()
                            if (priceInt == null) {
                                Toast.makeText(context, "Harga harus angka", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isUpdating = true
                            // Perbaiki tipe data: pastikan drinkCategory String? tetap String?
                            viewModel.updateMenu(
                                context = context,
                                id = menuId,
                                name = name,
                                price = price,
                                category = category,
                                drinkCat = if (category == "Drink") drinkCategory else null,
                                desc = description,
                                imageUri = imageUri
                            ) { success, message ->
                                isUpdating = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Update Menu")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}