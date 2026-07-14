package com.example.cafeapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        findViewById<Button>(R.id.btnStaff).setOnClickListener {
            saveRoleAndNavigate("STAFF")
        }

        findViewById<Button>(R.id.btnAdmin).setOnClickListener {
            saveRoleAndNavigate("ADMIN")
        }
    }


    private fun saveRoleAndNavigate(role: String, tableNum: String? = null) {

        val sharedPrefs = getSharedPreferences("CafeConfig", Context.MODE_PRIVATE)

        with(sharedPrefs.edit()) {
            putString("DEVICE_ROLE", role)
            if (tableNum != null) {
                putString("TABLE_NUMBER", tableNum)
            }
            apply()
        }

        startActivity(Intent(this, RouterActivity::class.java))
        finish()
    }
}