package com.example.cafeapp.feature.staff

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R
import com.example.cafeapp.viewmodel.CartDetailViewModel
import kotlinx.coroutines.launch

class CartDetailActivity : AppCompatActivity() {

    private val viewModel: CartDetailViewModel by viewModels()
    private lateinit var adapter: CartDetailAdapter
    private var tableNumber: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_detail)

        tableNumber = intent.getStringExtra("EXTRA_TABLE_NUMBER") ?: "0"
        findViewById<TextView>(R.id.tvCartHeader).text = "Cart Details - Table #$tableNumber"

        setupRecyclerView()
        observeViewModel()

        findViewById<Button>(R.id.btnFinalCheckout).setOnClickListener {
            currentFocus?.clearFocus()

            val staffId = "b3a1c8f2-9d4e-4b2a-8f1c-7e3d9a2b5c4f"
            viewModel.checkoutCart(tableNumber, staffId)
        }
    }

    private fun setupRecyclerView() {
        adapter = CartDetailAdapter(
            onQuantityChange = { item, isIncrease -> viewModel.updateQuantity(item, isIncrease) },
            onCustomizationSave = { item, text -> viewModel.updateCustomization(item, text) },
            onRemove = { item -> viewModel.removeItem(item) }
        )
        findViewById<RecyclerView>(R.id.rvCartItems).apply {
            layoutManager = LinearLayoutManager(this@CartDetailActivity)
            this.adapter = this@CartDetailActivity.adapter
        }
    }

    private fun observeViewModel() {
        val tvTotal = findViewById<TextView>(R.id.tvCartFinalTotal)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.liveCart.collect { items ->
                        adapter.submitList(items)
                        val total = items.sumOf { it.price * it.quantity }
                        tvTotal.text = "Rp ${total.toInt()}"
                        if (items.isEmpty() && !isFinishing) {
                            Toast.makeText(this@CartDetailActivity, "Cart is empty", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                launch {
                    viewModel.checkoutResult.collect { success ->
                        if (success) {
                            Toast.makeText(this@CartDetailActivity, "Order sent to kitchen!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@CartDetailActivity, "Failed to send order.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}