package com.example.cafeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cafeapp.feature.splash.SplashScreen
import com.example.cafeapp.feature.setup.SetupScreen
import com.example.cafeapp.feature.admin.dashboard.AdminDashboardScreen
import com.example.cafeapp.feature.staff.dashboard.StaffDashboardScreen
import com.example.cafeapp.feature.staff.menu.StaffMenuScreen
import com.example.cafeapp.feature.staff.cart.CartDetailScreen
import com.example.cafeapp.feature.staff.tablestatus.StaffTableScreen

@Composable
fun CafeAppNavigation(
    startRole: String?, // This comes from DataStore via MainActivity
    onRoleSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // 1. Splash is the absolute entry point every time the app opens
    NavHost(navController = navController, startDestination = AppRoutes.SplashRoute) {

        // ==========================================
        // 0. SPLASH SCREEN
        // ==========================================
        composable<AppRoutes.SplashRoute> {
            SplashScreen(
                onTimeout = {
                    // 2. Evaluate the DataStore role AFTER the splash finishes
                    val destination: Any = when (startRole) {
                        "STAFF" -> AppRoutes.StaffDashboardRoute
                        "ADMIN" -> AppRoutes.AdminDashboardRoute
                        else -> AppRoutes.SetupRoute
                    }

                    navController.navigate(destination) {
                        // 3. Destroy the splash screen from the backstack
                        popUpTo(AppRoutes.SplashRoute) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // 1. SETUP SCREEN
        // ==========================================
        composable<AppRoutes.SetupRoute> {
            SetupScreen(
                onRoleSelected = { role ->
                    onRoleSelected(role) // Persist to DataStore

                    val destination: Any = if (role == "STAFF") AppRoutes.StaffDashboardRoute else AppRoutes.AdminDashboardRoute
                    navController.navigate(destination) {
                        // Prevent the user from going back to Setup
                        popUpTo(AppRoutes.SetupRoute) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // 2. ADMIN FLOW
        // ==========================================
        composable<AppRoutes.AdminDashboardRoute> {
            AdminDashboardScreen(
                onLogoutClicked = {
                    onLogout() // Clear from DataStore
                    navController.navigate(AppRoutes.SetupRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
                onTableStatusClicked = {
                    navController.navigate(AppRoutes.StaffTableStatusRoute)
                },
                onLogoutClicked = {
                    onLogout()
                    navController.navigate(AppRoutes.SetupRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<AppRoutes.StaffTableStatusRoute> {
            StaffTableScreen(
                onBackClicked = { navController.popBackStack() }
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