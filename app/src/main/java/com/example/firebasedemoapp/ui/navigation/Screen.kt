package com.example.firebasedemoapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object CreateListing : Screen("create_listing")
    object ChatList : Screen("chat_list")
    object ChatDetail : Screen("chat_detail/{otherUserId}") {
        fun createRoute(otherUserId: String) = "chat_detail/$otherUserId"
    }
    object Profile : Screen("profile")
}
