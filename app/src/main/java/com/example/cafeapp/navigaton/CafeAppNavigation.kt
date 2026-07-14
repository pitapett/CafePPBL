package com.example.cafeapp.navigaton

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cafeapp.feature.setup.SetupScreen

import com.example.cafeapp.feature.setup.SetupScreen
import com.example.cafeapp.feature.admin.dashboard.AdminDashboardScreen
import com.example.cafeapp.feature.admin.stock.ManageStockScreen
import com.example.cafeapp.feature.admin.tables.ManageTablesScreen

@Composable
fun CafeAppNavigation(startRole: String?) {
    val navController = rememberNavController()

    // Determine the starting screen based on the saved role
    val startDest = when (startRole) {
        "STAFF" -> StaffDashboardRoute
        "ADMIN" -> AdminDashboardRoute
        else -> SetupRoute
    }

    NavHost(navController = navController, startDestination = startDest) {

        // 1. Setup Screen
        composable<SetupRoute> {
            SetupScreen(
                onRoleSelected = { role ->
                    // Save to SharedPreferences here, then navigate
                    if (role == "STAFF") navController.navigate(StaffDashboardRoute)
                    else navController.navigate(AdminDashboardRoute)
                }
            )
        }

        // Inside your NavHost { ... }

        composable<AdminDashboardRoute> {
            AdminDashboardScreen(
                onManageStockClicked = {
                    // Navigate to Manage Stock (assuming you created the route object)
                    navController.navigate(ManageStockRoute)
                },
                onManageTablesClicked = {
                    // Navigate to Manage Tables
                    navController.navigate(ManageTablesRoute)
                },
                onLogoutClicked = {
                    // Clear the backstack and go back to Setup
                    navController.navigate(SetupRoute) {
                        popUpTo(0) { inclusive = true } // Clears navigation history
                    }
                }
            )
        }

        composable<ManageStockRoute> {
            ManageStockScreen(
                onBackClicked = {
                    // Pops this screen off the stack, returning to AdminDashboard
                    navController.popBackStack()
                },
                onAddStockClicked = {
                    // navController.navigate(AddEditStockRoute(itemId = null))
                },
                onEditItemClicked = { itemId ->
                    // navController.navigate(AddEditStockRoute(itemId = itemId))
                }
            )
        }

        composable<ManageTablesRoute> {
            ManageTablesScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onAddTableClicked = {
                    // navController.navigate(AddEditTableRoute)
                },
                onTableClicked = { tableId ->
                    // navController.navigate(TableDetailRoute(tableId))
                }
            )
        }

        // ... Register all other routes here
    }
}
