package com.example.cafeapp.navigaton

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

        // 2. Staff Dashboard
        composable<StaffDashboardRoute> {
            StaffDashboardScreen(
                onNewOrderClicked = { tableNum -> navController.navigate(StaffMenuRoute(tableNum)) },
                onActiveOrdersClicked = { navController.navigate(ActiveOrdersRoute) },
                onTableStatusClicked = { navController.navigate(StaffTableStatusRoute) }
            )
        }

        // 3. Staff Menu (Extracting the argument)
        composable<StaffMenuRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<StaffMenuRoute>()
            StaffMenuScreen(
                tableNumber = route.tableNumber,
                onViewDetailsClicked = { navController.navigate(CartDetailRoute(route.tableNumber)) }
            )
        }

        // ... Register all other routes here
    }
}
