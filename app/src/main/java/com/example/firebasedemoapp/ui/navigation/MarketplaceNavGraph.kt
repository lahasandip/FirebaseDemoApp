package com.example.firebasedemoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebasedemoapp.ui.screens.auth.LoginScreen
import com.example.firebasedemoapp.ui.screens.auth.ProfileScreen
import com.example.firebasedemoapp.ui.screens.chat.ChatListScreen
import com.example.firebasedemoapp.ui.screens.chat.ChatScreen
import com.example.firebasedemoapp.ui.screens.home.HomeScreen
import com.example.firebasedemoapp.ui.screens.listing.ProductDetailScreen
import com.example.firebasedemoapp.ui.screens.listing.CreateListingScreen
import com.example.firebasedemoapp.viewmodel.AuthViewModel
import com.example.firebasedemoapp.viewmodel.HomeViewModel

@Composable
fun MarketplaceNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            val products by viewModel.products.collectAsState()
            val showPromo by viewModel.showPromo.collectAsState()
            val promoMessage by viewModel.promoMessage.collectAsState()
            val notificationCount by viewModel.notificationCount.collectAsState()
            val authViewModel: AuthViewModel = viewModel()

            HomeScreen(
                products = products,
                showPromo = showPromo,
                promoMessage = promoMessage,
                notificationCount = notificationCount,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCreateListingClick = {
                    navController.navigate(Screen.CreateListing.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onNotificationsClick = {
                    navController.navigate(Screen.ChatList.route)
                },
                onLogoutClick = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
            ChatScreen(otherUserId = otherUserId, onBack = { navController.popBackStack() })
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onContactSeller = { sellerId ->
                    // For simplicity, chatId is sellerId
                    navController.navigate(Screen.ChatDetail.createRoute(sellerId))
                }
            )
        }
        composable(Screen.CreateListing.route) {
            CreateListingScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}
