package com.example.cafeapp.feature.admin

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R
import com.example.cafeapp.data.remote.dto.StockResponse
import com.example.cafeapp.utils.Resource
import com.example.cafeapp.viewmodel.ManageStockViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ManageStockActivity : AppCompatActivity() {

    private val viewModel: ManageStockViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_stock)

        setupRecyclerView()
        observeViewModel()

        findViewById<FloatingActionButton>(R.id.fabAddStock).setOnClickListener {
            showStockDialog(null)
        }

        viewModel.fetchStock()
    }
    
    private fun setupRecyclerView() {
        stockAdapter = StockAdapter(
            onEditClicked = { stock -> showStockDialog(stock) },
            onDeleteClicked = { stock ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Stock")
                    .setMessage("Are you sure you want to delete ${stock.ingredient_name}?")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteStock(stock.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        findViewById<RecyclerView>(R.id.rvStock).apply {
            layoutManager = LinearLayoutManager(this@ManageStockActivity)
            adapter = stockAdapter
        }
    }

    private fun showStockDialog(existingStock: StockResponse?) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val nameInput = EditText(this).apply {
            hint = "Ingredient Name"
            setText(existingStock?.ingredient_name ?: "")
        }

        val amountInput = EditText(this).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(existingStock?.amount?.toString() ?: "")
        }

        layout.addView(nameInput)
        layout.addView(amountInput)

        AlertDialog.Builder(this)
            .setTitle(if (existingStock == null) "Add Stock" else "Edit Stock")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                val amount = amountInput.text.toString().toIntOrNull() ?: 0

                if (name.isNotBlank()) {
                    if (existingStock == null) {
                        viewModel.addStock(name, amount)
                    } else {
                        viewModel.updateStock(existingStock.id, name, amount)
                    }
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stockList.collect { state ->
                        if (state is Resource.Success) {
                            stockAdapter.submitList(state.data)
                        } else if (state is Resource.Error) {
                            Toast.makeText(this@ManageStockActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                launch {
                    viewModel.actionStatus.collect { state ->
                        if (state is Resource.Success) {
                            Toast.makeText(this@ManageStockActivity, state.data, Toast.LENGTH_SHORT).show()
                            viewModel.resetActionStatus()
                        } else if (state is Resource.Error) {
                            Toast.makeText(this@ManageStockActivity, state.message, Toast.LENGTH_SHORT).show()
                            viewModel.resetActionStatus()
                        }
                    }
                }
            }
        }
    }
}