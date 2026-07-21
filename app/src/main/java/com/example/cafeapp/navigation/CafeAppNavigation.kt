package com.example.cafeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// Import semua screen yang dibutuhkan
import com.example.cafeapp.feature.setup.SetupScreen
import com.example.cafeapp.feature.admin.dashboard.AdminDashboardScreen
import com.example.cafeapp.feature.staff.dashboard.StaffDashboardScreen
import com.example.cafeapp.feature.staff.menu.StaffMenuScreen
import com.example.cafeapp.feature.staff.cart.CartDetailScreen

@Composable
fun CafeAppNavigation(startRole: String? = null) {
    val navController = rememberNavController()

    // Menentukan rute awal berdasarkan role
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
            // Karena AdminDashboardScreen sekarang punya BottomNav sendiri,
            // rute ManageStock dan ManageTables dihapus dari sini.
            // Kita hanya perlu menangani Logout.
            AdminDashboardScreen(
                onLogoutClicked = {
                    navController.navigate(AppRoutes.SetupRoute) {
                        popUpTo(0) { inclusive = true } // Hapus semua history backstack
                    }
                }
            )
        }

        // ==========================================
        // 3. STAFF FLOW
        // ==========================================
        composable<AppRoutes.StaffDashboardRoute> {
            // Gunakan nama fungsi dan parameter yang benar sesuai kodemu
            StaffDashboardScreen(
                onNewOrderClicked = { tableNum ->
                    navController.navigate(AppRoutes.StaffMenuRoute(tableNumber = tableNum))
                },
                onLogoutClicked = {
                    navController.navigate(AppRoutes.SetupRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<AppRoutes.StaffMenuRoute> { backStackEntry ->
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
                    navController.navigate(AppRoutes.StaffDashboardRoute) {
                        popUpTo(AppRoutes.StaffDashboardRoute) { inclusive = false }
                    }
                }
            )
        }


    }
}