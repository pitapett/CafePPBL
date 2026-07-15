package com.example.cafeapp.navigaton // (or navigation if you renamed it)

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// Import all your screens
import com.example.cafeapp.feature.setup.SetupScreen
import com.example.cafeapp.feature.admin.dashboard.AdminDashboardScreen
import com.example.cafeapp.feature.admin.stock.ManageStockScreen
import com.example.cafeapp.feature.admin.tables.ManageTablesScreen
import com.example.cafeapp.feature.staff.dashboard.StaffDashboardScreen
import com.example.cafeapp.feature.staff.menu.StaffMenuScreen
import com.example.cafeapp.feature.staff.cart.CartDetailScreen
import com.example.cafeapp.feature.staff.orders.ActiveOrdersScreen
import com.example.cafeapp.feature.staff.tablestatus.StaffTableStatusScreen

@Composable
fun CafeAppNavigation(startRole: String? = null) {
    val navController = rememberNavController()

    // Determine the starting screen based on login/setup state
    val startDest: Any = when (startRole) {
        "STAFF" -> AppRoutes.StaffDashboardRoute
        "ADMIN" -> AppRoutes.AdminDashboardRoute
        else -> AppRoutes.SetupRoute
    }

    NavHost(navController = navController, startDestination = startDest) {

        // ==========================================
        // 1. SETUP SCREEN
        // ==========================================
        composable<AppRoutes.SetupRoute> {
            SetupScreen(
                onRoleSelected = { role ->
                    if (role == "STAFF") {
                        navController.navigate(AppRoutes.StaffDashboardRoute)
                    } else {
                        navController.navigate(AppRoutes.AdminDashboardRoute)
                    }
                }
            )
        }

        // ==========================================
        // 2. ADMIN FLOW
        // ==========================================
        composable<AppRoutes.AdminDashboardRoute> {
            AdminDashboardScreen(
                onManageStockClicked = { navController.navigate(AppRoutes.ManageStockRoute) },
                onManageTablesClicked = { navController.navigate(AppRoutes.ManageTablesRoute) },
                onLogoutClicked = {
                    navController.navigate(AppRoutes.SetupRoute) {
                        popUpTo(0) { inclusive = true } // Clears backstack completely
                    }
                }
            )
        }

        composable<AppRoutes.ManageStockRoute> {
            ManageStockScreen(
                onBackClicked = { navController.popBackStack() },
                onAddStockClicked = { /* TODO: Open Add Dialog/Screen */ },
                onEditItemClicked = { itemId -> /* TODO: Open Edit Dialog/Screen */ }
            )
        }

        composable<AppRoutes.ManageTablesRoute> {
            ManageTablesScreen(
                onBackClicked = { navController.popBackStack() },
                onAddTableClicked = { /* TODO: Open Add Dialog/Screen */ },
                onTableClicked = { tableId -> /* TODO: Open Edit Dialog/Screen */ }
            )
        }

        // ==========================================
        // 3. STAFF FLOW
        // ==========================================
        composable<AppRoutes.StaffDashboardRoute> {
            StaffDashboardScreen(
                onNewOrderClicked = { tableNum ->
                    navController.navigate(AppRoutes.StaffMenuRoute(tableNumber = tableNum))
                },
                onActiveOrdersClicked = { navController.navigate(AppRoutes.ActiveOrdersRoute) },
                onTableStatusClicked = { navController.navigate(AppRoutes.StaffTableStatusRoute) },
                onLogoutClicked = {
                    navController.navigate(AppRoutes.SetupRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<AppRoutes.StaffMenuRoute> { backStackEntry ->
            // Extract the arguments using Kotlin Serialization
            val route = backStackEntry.toRoute<AppRoutes.StaffMenuRoute>()

            StaffMenuScreen(
                tableNumber = route.tableNumber,
                onBackClicked = { navController.popBackStack() },
                onViewCartClicked = {
                    navController.navigate(AppRoutes.CartDetailRoute(route.tableNumber))
                }
            )
        }

        composable<AppRoutes.CartDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.CartDetailRoute>()

            CartDetailScreen(
                tableNumber = route.tableNumber,
                onBackClicked = { navController.popBackStack() },
                onCheckoutSuccess = {
                    // Navigate to Dashboard and clear the Menu/Cart from history
                    navController.navigate(AppRoutes.StaffDashboardRoute) {
                        popUpTo(AppRoutes.StaffDashboardRoute) { inclusive = false }
                    }
                }
            )
        }

        composable<AppRoutes.ActiveOrdersRoute> {
            ActiveOrdersScreen(
                onBackClicked = { navController.popBackStack() },
                onOrderClicked = { orderId ->
                    // Optional: Navigate to a detailed view of the specific order
                }
            )
        }

        composable<AppRoutes.StaffTableStatusRoute> {
            StaffTableStatusScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}