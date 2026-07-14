package com.example.cafeapp.feature.staff

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cafeapp.viewmodel.ActiveOrdersViewModel
import com.example.cafeapp.viewmodel.StaffTableViewModel

// Warna asli diambil persis dari colors.xml project (cafe_navy, cafe_pink)
private val CafeNavy = Color(0xFF021A54)
private val CafePink = Color(0xFFFF85BB)

/**
 * Pengganti StaffDashboardActivity versi 3-tombol sebelumnya.
 *
 * Sekarang jadi host untuk 2 tab via Bottom Navigation (Active Orders & Table Status)
 * plus FAB untuk aksi "New Order" -- sesuai arahan dosen untuk pakai komponen
 * navigasi Material Design (Bottom Navigation + FAB), bukan tombol biasa.
 *
 * "New Order" sengaja dijadikan FAB (bukan tab ketiga) karena sifatnya aksi
 * sesaat (munculin dialog input meja), bukan screen/section yang punya
 * konten untuk di-browse seperti 2 tab lainnya.
 */
class StaffDashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StaffMainScreen(
                onNewOrder = { tableNumber ->
                    val intent = Intent(this, StaffActivity::class.java)
                    intent.putExtra("EXTRA_TABLE_NUMBER", tableNumber)
                    startActivity(intent)
                },
            )
        }
    }
}

private sealed class StaffTab(val route: String, val label: String) {
    data object ActiveOrders : StaffTab("active_orders", "Active Orders")
    data object TableStatus : StaffTab("table_status", "Table Status")
}

private val staffTabs = listOf(StaffTab.ActiveOrders, StaffTab.TableStatus)

@Composable
fun StaffMainScreen(
    onNewOrder: (tableNumber: String) -> Unit,
) {
    val navController = rememberNavController()
    var showTableInputDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { StaffBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTableInputDialog = true },
                containerColor = CafeNavy,
                contentColor = Color.White,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New Order")
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = StaffTab.ActiveOrders.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(StaffTab.ActiveOrders.route) {
                val viewModel: ActiveOrdersViewModel = viewModel()
                // fetchActiveOrders dipanggil sekali setiap kali tab ini pertama dibuka.
                LaunchedEffect(Unit) {
                    viewModel.fetchActiveOrders()
                }
                ActiveOrdersScreen(viewModel)
            }
            composable(StaffTab.TableStatus.route) {
                val viewModel: StaffTableViewModel = viewModel()
                LaunchedEffect(Unit) {
                    viewModel.fetchTables()
                }
                StaffTableScreen(viewModel)
            }
        }
    }

    if (showTableInputDialog) {
        TableInputDialog(
            onDismiss = { showTableInputDialog = false },
            onConfirm = { tableNumber ->
                showTableInputDialog = false
                onNewOrder(tableNumber)
            },
        )
    }
}

@Composable
private fun StaffBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        staffTabs.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.route) {
                        // Hindari numpuk banyak instance tab yang sama di back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            StaffTab.ActiveOrders -> Icons.Filled.List
                            StaffTab.TableStatus -> Icons.Filled.TableRestaurant
                        },
                        contentDescription = tab.label,
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CafeNavy,
                    selectedTextColor = CafeNavy,
                    indicatorColor = CafePink,
                ),
            )
        }
    }
}

/**
 * Pengganti showTableInputDialog() lama (AlertDialog.Builder + EditText).
 * Sama seperti sebelumnya -- dipindah ke sini karena sekarang FAB ada di level
 * StaffMainScreen, bukan lagi di salah satu "tombol" dashboard.
 */
@Composable
private fun TableInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var tableNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Order") },
        text = {
            Column {
                Text("Enter the customer's table number:")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = {
                        tableNumber = it
                        showError = false
                    },
                    placeholder = { Text("Table Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showError,
                    singleLine = true,
                )
                if (showError) {
                    Text(
                        text = "Table number is required",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (tableNumber.isNotBlank()) {
                    onConfirm(tableNumber)
                } else {
                    showError = true
                }
            }) {
                Text("Start Order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}