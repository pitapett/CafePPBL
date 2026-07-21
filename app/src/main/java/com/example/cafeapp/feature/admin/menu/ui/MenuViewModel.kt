package com.example.cafeapp.feature.admin.menu.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MenuViewModel : ViewModel() {
    private val api = RetrofitClient.api

    // Kamu bisa menambahkan State untuk UI (Loading/Success/Error) di sini nanti
    fun createMenu(
        context: Context,
        name: String,
        price: String,
        category: String,
        drinkCat: String,
        desc: String,
        imageUri: Uri,
        onResult: (Boolean, String) -> Unit // Callback untuk memberi tahu UI hasil prosesnya
    ) {
        viewModelScope.launch {
            try {
                // 1. Konversi data teks ke RequestBody
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val pricePart = price.toRequestBody("text/plain".toMediaType())
                val catPart = category.toRequestBody("text/plain".toMediaType())
                val drinkPart = drinkCat.toRequestBody("text/plain".toMediaType())
                val descPart = desc.toRequestBody("text/plain".toMediaType())

                // 2. Konversi file gambar ke MultipartBody.Part
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(imageUri)
                val file = File(context.cacheDir, "temp_image.jpg")
                inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // 3. Panggil API
                val response = api.createMenu(namePart, pricePart, catPart, drinkPart, descPart, imagePart)

                if (response.isSuccessful) {
                    onResult(true, "Menu berhasil dibuat!")
                } else {
                    onResult(false, "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Terjadi kesalahan koneksi: ${e.message}")
            }
        }
    }
}