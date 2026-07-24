package com.example.cafeapp.navigation

import kotlinx.serialization.Serializable

object AppRoutes {

    @Serializable
    object SplashRoute
    @Serializable
    object SetupRoute

    @Serializable
    object StaffTableStatusRoute

    // --- ADMIN ROUTES ---
    @Serializable
    object AdminDashboardRoute

    // --- STAFF ROUTES ---
    @Serializable
    object StaffDashboardRoute

    @Serializable
    data class StaffMenuRoute(val tableNumber: String)

    @Serializable
    data class CartDetailRoute(val tableNumber: String)
}