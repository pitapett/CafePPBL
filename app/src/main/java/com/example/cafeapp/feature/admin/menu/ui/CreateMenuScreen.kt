package com.example.cafeapp.feature.admin.menu.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreateMenuScreen(
    onNavigateBack: () -> Unit,
    viewModel: MenuViewModel = viewModel()) {
    val context = LocalContext.current

    // State untuk input field
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") } // Tambahkan deskripsi
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk loading (opsional)
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nama Menu") }, modifier = Modifier.fillMaxWidth())
        TextField(value = price, onValueChange = { price = it }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth())
        TextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(if (imageUri == null) "Pilih Gambar" else "Gambar Terpilih")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && imageUri != null,
            onClick = {
                isLoading = true
                viewModel.createMenu(context, name, price, "Main", "Coffee", description, imageUri!!) { success, message ->
                    isLoading = false
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                    if (success) {
                        onNavigateBack()
                    }
                }
            }
        ) {
            if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            else Text("Simpan Menu")
        }
    }
}