package com.example.cafeapp.feature.staff.tablestatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cafeapp.utils.Resource
import com.example.cafeapp.feature.staff.tablestatus.StaffTableViewModel
import com.example.cafeapp.data.remote.dto.TableResponse

class StaffTableStatusActivity : ComponentActivity() {

    private val viewModel: StaffTableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchTables()

        setContent {
            StaffTableScreen(viewModel)
        }
    }
}

@Composable
fun StaffTableScreen(viewModel: StaffTableViewModel) {
    val state by viewModel.tables.collectAsStateWithLifecycle()

    when (state) {
        is Resource.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize()
            )
        }

        is Resource.Success -> {
            val tables: List<TableResponse> = (state as Resource.Success).data ?: emptyList()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(tables) { table ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Table ${table.tableNumber}")
                                Text(text = "Area: ${table.area}")
                                Text(text = "Seats: ${table.seatCount}")
                            }

                            Switch(
                                checked = table.status == "Available",
                                onCheckedChange = { newValue ->
                                    viewModel.toggleTableStatus(
                                        table.id,
                                        newValue
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        is Resource.Error -> {
            Text(
                text = (state as Resource.Error).message ?: "Unknown Error"
            )
        }

        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
    }
}