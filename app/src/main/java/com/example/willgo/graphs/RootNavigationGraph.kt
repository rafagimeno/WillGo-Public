package com.example.willgo.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.willgo.view.screens.MainScreen

@Composable
fun RootNavigationGraph(){
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = Graph.MAIN,
        route = Graph.ROOT)
    {
        //authNavGraph(navController)
        composable(route = Graph.MAIN){
            MainScreen()
        }
    }
}

object Graph{
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
    const val DETAIL = "detail_graph"
}