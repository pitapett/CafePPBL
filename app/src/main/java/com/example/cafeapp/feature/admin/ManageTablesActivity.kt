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
import com.example.cafeapp.data.remote.dto.TableResponse
import com.example.cafeapp.utils.Resource
import com.example.cafeapp.viewmodel.ManageTablesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ManageTablesActivity : AppCompatActivity() {

    private val viewModel: ManageTablesViewModel by viewModels()
    private lateinit var adapter: ManageTablesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tables)

        setupRecyclerView()
        observeViewModel()

        findViewById<FloatingActionButton>(R.id.fabAddTable).setOnClickListener {
            showTableDialog(null)
        }

        viewModel.fetchTables()
    }

    private fun setupRecyclerView() {
        adapter = ManageTablesAdapter(
            onEditClicked = { table -> showTableDialog(table) },
            onDeleteClicked = { table -> confirmDelete(table) }
        )
        findViewById<RecyclerView>(R.id.rvTables).apply {
            layoutManager = LinearLayoutManager(this@ManageTablesActivity)
            this.adapter = this@ManageTablesActivity.adapter
        }
    }

    private fun showTableDialog(tableToEdit: TableResponse?) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etTableNumber = EditText(this).apply {
            hint = "Table Number"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(tableToEdit?.tableNumber?.toString() ?: "")
        }

        val etArea = EditText(this).apply {
            hint = "Area (e.g., Indoor, VIP, Smoking)"
            inputType = InputType.TYPE_CLASS_TEXT
            setText(tableToEdit?.area ?: "")
        }

        layout.addView(etTableNumber)
        layout.addView(etArea)

        val title = if (tableToEdit == null) "Add New Table" else "Edit Table"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val tableNumStr = etTableNumber.text.toString()
                val area = etArea.text.toString()

                if (tableNumStr.isNotBlank() && area.isNotBlank()) {
                    val tableNumber = tableNumStr.toInt()
                    if (tableToEdit == null) {
                        viewModel.addTable(tableNumber, area)
                    } else {
                        viewModel.updateTable(tableToEdit.id, tableNumber, area)
                    }
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(table: TableResponse) {
        AlertDialog.Builder(this)
            .setTitle("Delete Table")
            .setMessage("Are you sure you want to delete Table ${table.tableNumber}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTable(table.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tables.collect { state ->
                        if (state is Resource.Success) {
                            adapter.submitList(state.data)
                        } else if (state is Resource.Error) {
                            Toast.makeText(this@ManageTablesActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                launch {
                    viewModel.actionStatus.collect { state ->
                        if (state is Resource.Success) {
                            Toast.makeText(this@ManageTablesActivity, state.data, Toast.LENGTH_SHORT).show()
                            viewModel.resetActionStatus()
                        } else if (state is Resource.Error) {
                            Toast.makeText(this@ManageTablesActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetActionStatus()
                        }
                    }
                }
            }
        }
    }
}