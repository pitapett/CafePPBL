package com.example.cafeapp.feature.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeapp.R

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        findViewById<Button>(R.id.btnManageStock).setOnClickListener {
            startActivity(Intent(this, ManageStockActivity::class.java))
        }

        findViewById<Button>(R.id.btnManageTables).setOnClickListener {
            startActivity(Intent(this, ManageTablesActivity::class.java))
        }
    }
}