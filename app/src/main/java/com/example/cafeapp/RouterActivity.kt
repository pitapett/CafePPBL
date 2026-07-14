// RouterActivity.kt
package com.example.cafeapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeapp.feature.admin.AdminActivity
import com.example.cafeapp.feature.staff.StaffDashboardActivity

class RouterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routeDevice()
    }

    private fun routeDevice() {
        val sharedPrefs = getSharedPreferences("CafeConfig", Context.MODE_PRIVATE)
        val deviceRole = sharedPrefs.getString("DEVICE_ROLE", null)

        when (deviceRole) {
            "STAFF" -> {
                startActivity(Intent(this, StaffDashboardActivity::class.java)) // Changed from StaffActivity
            }
            "ADMIN" -> {
                 startActivity(Intent(this, AdminActivity::class.java))
            }
            else -> {
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SetupActivity::class.java))
                finish()
            }
        }

        finish()
    }
}