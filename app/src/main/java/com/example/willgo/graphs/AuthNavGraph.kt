package com.example.willgo.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.willgo.view.screens.other.SplashScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(route = Graph.AUTH,
        startDestination = AuthScreen.Splash.route){

        composable(route = AuthScreen.Splash.route){
            SplashScreen(navController)
        }

    }
}

sealed class AuthScreen(val route: String){
    object Splash: AuthScreen("splash_screen")
}