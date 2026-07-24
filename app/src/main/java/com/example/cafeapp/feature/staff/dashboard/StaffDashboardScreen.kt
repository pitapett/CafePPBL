package com.example.cafeapp.feature.staff.dashboard

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

// Import screens dan ViewModels beserta Factory-nya
import com.example.cafeapp.feature.staff.orders.ActiveOrdersScreen
import com.example.cafeapp.feature.staff.orders.ActiveOrdersViewModel
import com.example.cafeapp.feature.staff.orders.ActiveOrdersViewModelFactory
import com.example.cafeapp.feature.staff.tablestatus.StaffTableScreen
import com.example.cafeapp.feature.staff.tablestatus.StaffTableViewModel
import com.example.cafeapp.feature.staff.tablestatus.StaffTableViewModelFactory

private val CafeNavy = Color(0xFF021A54)
private val CafePink = Color(0xFFFF85BB)

private sealed class StaffTab(val route: String, val label: String) {
    data object ActiveOrders : StaffTab("active_orders", "Active Orders")
    data object TableStatus : StaffTab("table_status", "Table Status")
}

private val staffTabs = listOf(StaffTab.ActiveOrders, StaffTab.TableStatus)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    onNewOrderClicked: (String) -> Unit,
    onLogoutClicked: () -> Unit,
    onTableStatusClicked: () -> Unit = {}
) {
    val bottomNavController = rememberNavController()
    var showTableInputDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Dashboard") },
                actions = {
                    IconButton(onClick = onLogoutClicked) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = { StaffBottomBar(bottomNavController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showTableInputDialog = true
                },
                containerColor = CafeNavy,
                contentColor = Color.White,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New Order")
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = bottomNavController,
            startDestination = StaffTab.ActiveOrders.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            // TAB 1: ACTIVE ORDERS
            composable(StaffTab.ActiveOrders.route) {
                val context = LocalContext.current
                val application = context.applicationContext as Application

                val viewModel: ActiveOrdersViewModel = viewModel(
                    factory = ActiveOrdersViewModelFactory(application)
                )

                LaunchedEffect(Unit) {
                    viewModel.fetchActiveOrders()
                }

                ActiveOrdersScreen(viewModel)
            }

            // TAB 2: TABLE STATUS
            composable(StaffTab.TableStatus.route) {
                val context = LocalContext.current
                val application = context.applicationContext as Application

                // ✅ PERBAIKAN: Gunakan Custom Factory (StaffTableViewModelFactory)
                val viewModel: StaffTableViewModel = viewModel(
                    factory = StaffTableViewModelFactory(application)
                )

                LaunchedEffect(Unit) {
                    viewModel.fetchTables()
                }

                StaffTableScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Modal dialog dimunculkan saat tombol FAB (+) diklik
    if (showTableInputDialog) {
        TableInputDialog(
            onDismiss = { showTableInputDialog = false },
            onConfirm = { tableNumber ->
                showTableInputDialog = false
                onNewOrderClicked(tableNumber)
            }
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
        }
    )
}