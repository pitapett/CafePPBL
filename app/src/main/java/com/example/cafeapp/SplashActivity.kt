package com.example.cafeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Warna asli diambil persis dari colors.xml project (cafe_navy, white)
private val CafeNavy = Color(0xFF021A54)
private val CafeWhite = Color(0xFFFFFFFF)

/**
 * Splash screen sebelum RouterActivity menentukan tujuan (Setup/Admin/Staff).
 * Logic pembacaan DEVICE_ROLE tetap di RouterActivity -- Splash tidak menduplikasi
 * logic itu, cuma menunda sebentar lalu meneruskan ke RouterActivity.
 */
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen(
                onTimeout = {
                    startActivity(Intent(this, RouterActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    splashDurationMillis: Long = 1500L,
) {
    // LaunchedEffect dengan Unit key hanya jalan sekali saat composable pertama kali muncul,
    // tidak berulang setiap recomposition.
    LaunchedEffect(Unit) {
        delay(splashDurationMillis)
        onTimeout()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CafeNavy,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Cafe App",
                color = CafeWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}