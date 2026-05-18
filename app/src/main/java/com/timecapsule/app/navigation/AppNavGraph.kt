package com.timecapsule.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.timecapsule.app.ui.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("main") {
            MainScreen(navController)
        }
        composable("create") {
            CreateCapsuleScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("analytics") {
            AnalyticsScreen(navController)
        }
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CapsuleDetailScreen(id, navController)
        }

        composable("emotion/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            EmotionScreen(id, navController)
        }
    }
}