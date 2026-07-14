package com.example.cafeapp.navigaton

import kotlinx.serialization.Serializable

// Screens with no arguments
@Serializable object SetupRoute
@Serializable object AdminDashboardRoute
@Serializable object ManageStockRoute
@Serializable object ManageTablesRoute
@Serializable object StaffDashboardRoute
@Serializable object ActiveOrdersRoute
@Serializable object StaffTableStatusRoute

// Screens that require arguments (Replaces intent.putExtra)
@Serializable data class StaffMenuRoute(val tableNumber: String)
@Serializable data class CartDetailRoute(val tableNumber: String)
