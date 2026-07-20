package com.example.cafeapp.navigaton

import kotlinx.serialization.Serializable

object AppRoutes {
    @Serializable object SetupRoute

    // --- ADMIN ROUTES ---
    @Serializable object AdminDashboardRoute
    @Serializable object ManageStockRoute
    @Serializable object ManageTablesRoute

    // --- STAFF ROUTES ---
    @Serializable object StaffDashboardRoute
    @Serializable data class StaffMenuRoute(val tableNumber: String)
    @Serializable data class CartDetailRoute(val tableNumber: String)
    @Serializable object ActiveOrdersRoute
    @Serializable object StaffTableStatusRoute
}
