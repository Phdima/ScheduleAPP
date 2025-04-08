package com.example.scheduleapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.scheduleapp.presentation.screens.MainScreen


val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main_graph"
    ) {
        navigation(startDestination = "MainScreen", route = "main_graph") {
            composable("MainScreen") {
                MainScreen()
            }
        }
    }
}