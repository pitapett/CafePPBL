package com.example.cafeapp.navigaton

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cafeapp.feature.setup.SetupScreen

import com.example.cafeapp.feature.setup.SetupScreen
import com.example.cafeapp.feature.admin.dashboard.AdminDashboardScreen

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



        // ... Register all other routes here
    }
}
