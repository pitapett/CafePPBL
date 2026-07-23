package com.example.cafeapp.feature.admin.menu.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafeapp.data.remote.RetrofitClient
import com.example.cafeapp.data.remote.dto.MenuResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MenuViewModel : ViewModel() {
    private val api = RetrofitClient.api

    // State untuk EditMenuScreen
    private val _menu = MutableStateFlow<MenuResponse?>(null)
    val menu: StateFlow<MenuResponse?> = _menu

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ========== GET MENU BY ID ==========
    fun getMenuById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.getMenuById(id)
                if (response.isSuccessful) {
                    _menu.value = response.body()
                } else {
                    _error.value = "Gagal memuat data: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan koneksi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== CREATE MENU (sudah ada) ==========
    fun createMenu(
        context: Context,
        name: String,
        price: String,
        category: String,
        drinkCat: String,
        desc: String,
        imageUri: Uri,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val pricePart = price.toRequestBody("text/plain".toMediaType())
                val catPart = category.toRequestBody("text/plain".toMediaType())
                val drinkPart = drinkCat.toRequestBody("text/plain".toMediaType())
                val descPart = desc.toRequestBody("text/plain".toMediaType())

                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(imageUri)
                val file = File(context.cacheDir, "temp_image.jpg")
                inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

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

    // ========== UPDATE MENU (sudah ada) ==========
    fun updateMenu(
        context: Context,
        id: String,
        name: String,
        price: String,
        category: String,
        drinkCat: String?,   // nullable
        desc: String,
        imageUri: Uri?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val pricePart = price.toRequestBody("text/plain".toMediaType())
                val catPart = category.toRequestBody("text/plain".toMediaType())
                val drinkPart = drinkCat?.toRequestBody("text/plain".toMediaType()) // nullable
                val descPart = desc.toRequestBody("text/plain".toMediaType())

                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val file = File(context.cacheDir, "temp_image.jpg")
                    inputStream?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                val response = api.updateMenu(
                    id = id,
                    name = namePart,
                    price = pricePart,
                    category = catPart,
                    drinkCategory = drinkPart, // nullable
                    description = descPart,
                    image = imagePart
                )

                if (response.isSuccessful) {
                    onResult(true, "Menu berhasil diupdate!")
                } else {
                    onResult(false, "Error ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }

    fun deleteMenu(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.deleteMenu(id)
                if (response.isSuccessful) {
                    onResult(true, "Menu berhasil dihapus!")
                } else {
                    onResult(false, "Gagal hapus: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Error: ${e.message}")
            }
        }
    }

}