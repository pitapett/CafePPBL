package com.example.cafeapp.feature.admin.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cafeapp.feature.admin.stock.ManageStockScreen
import com.example.cafeapp.feature.admin.tables.ManageTablesScreen

private val CafeNavy = Color(0xFF021A54)
private val CafePink = Color(0xFFFFFF85BB) // Assuming this is your pink color

private sealed class AdminTab(val route: String, val label: String) {
    data object Stock : AdminTab("admin_stock", "Manage Stock")
    data object Tables : AdminTab("admin_tables", "Manage Tables")
}

private val adminTabs = listOf(AdminTab.Stock, AdminTab.Tables)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogoutClicked: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onLogoutClicked) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = { AdminBottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminTab.Stock.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AdminTab.Stock.route) {
                ManageStockScreen()
            }
            composable(AdminTab.Tables.route) {
                ManageTablesScreen()
            }
        }
    }
}

@Composable
private fun AdminBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        adminTabs.forEach { tab ->
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
                            AdminTab.Stock -> Icons.Filled.Inventory
                            AdminTab.Tables -> Icons.Filled.TableRestaurant
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