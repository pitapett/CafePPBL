package com.example.cafeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cafeapp.navigation.CafeAppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Ini akan langsung memanggil fungsi setup/routing yang baru!
            CafeAppNavigation(startRole = null)
        }
    }
}