package com.example.cafeapp.feature.staff.tablestatus

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cafeapp.data.remote.dto.TableResponse
import com.example.cafeapp.utils.Resource

class StaffTableStatusScreen : ComponentActivity() {

    private val viewModel: StaffTableViewModel by viewModels {
        StaffTableViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StaffTableScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTableScreen(
    onBackClicked: () -> Unit = {},
    context: Application = LocalContext.current.applicationContext as Application,
    viewModel: StaffTableViewModel = viewModel(
        factory = StaffTableViewModelFactory(context)
    )
) {
    LaunchedEffect(Unit) {
        viewModel.fetchTables()
    }

    val state by viewModel.tables.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Meja") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val currentState = state) {
                is Resource.Idle -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                is Resource.Success -> {
                    val tables: List<TableResponse> = currentState.data ?: emptyList()

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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.message ?: "Unknown Error"
                        )
                    }
                }

                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}