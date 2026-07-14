// ui/staff/StaffActivity.kt
package com.example.cafeapp.ui.staff // Update package name

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Button
    import android.widget.LinearLayout
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
    import com.example.cafeapp.viewmodel.StaffMenuViewModel
    import kotlinx.coroutines.launch

    class StaffActivity : AppCompatActivity() {

    private val viewModel: StaffMenuViewModel by viewModels()
    private lateinit var menuAdapter: MenuAdapter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_staff)

            setupRecyclerView()
            observeViewModel()

            viewModel.syncMenuWithServer()

            // 1. Grab the table number passed from the initialization screen
            val tableNumber = intent.getStringExtra("EXTRA_TABLE_NUMBER") ?: "0"

            // 2. Set the header to show the table
            findViewById<TextView>(R.id.tvHeader).text = "Order for Table #$tableNumber"

            // 3. MODIFY THIS PART: Change the button to open the Cart Details screen
            val btnCheckout = findViewById<Button>(R.id.btnCheckout)
            btnCheckout.text = "View Details" // Update the button text to reflect its new job

            btnCheckout.setOnClickListener {
                // Open the Cart Detail Screen and securely pass the table number to it
                val intent = Intent(this, CartDetailActivity::class.java)
                intent.putExtra("EXTRA_TABLE_NUMBER", tableNumber)
                startActivity(intent)
            }
        }

        private fun setupRecyclerView() {
            val recyclerView = findViewById<RecyclerView>(R.id.rvStaffMenu)

            // Crucial: The adapter MUST receive the click lambda!
            menuAdapter = MenuAdapter { selectedMenu ->
                Log.d("CafeCart", "1. Click registered in Activity for: ${selectedMenu.name}")
                viewModel.addToCart(selectedMenu)
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@StaffActivity)
                adapter = menuAdapter
            }
        }

        private fun observeViewModel() {
            val layoutCart = findViewById<LinearLayout>(R.id.layoutBottomCart)
            val tvCount = findViewById<TextView>(R.id.tvCartCount)
            val tvTotal = findViewById<TextView>(R.id.tvCartTotal)

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    launch {
                        viewModel.menuState.collect { menus ->
                            menuAdapter.submitList(menus)
                        }
                    }

                    launch {
                        viewModel.liveCart.collect { cartItems ->
                            Log.w("CafeCart", "UI received cart update! Items in database: ${cartItems.size}")
                            if (cartItems.isEmpty()) {
                                layoutCart.visibility = View.GONE
                            } else {
                                layoutCart.visibility = View.VISIBLE
                                val totalItems = cartItems.sumOf { it.quantity }
                                val totalPrice = cartItems.sumOf { it.price * it.quantity }

                                tvCount.text = "$totalItems Items"
                                tvTotal.text = "Rp ${totalPrice.toInt()}"
                            }
                        }
                    }
                }
            }
        }
}