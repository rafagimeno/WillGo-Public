package com.example.willgo.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.willgo.data.Event
import com.example.willgo.view.screens.EventDataScreen
import com.example.willgo.view.screens.navScreens.MapScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Initialize the eventsState here
//    val events = remember { mutableStateOf<List<Event>>(emptyList()) }
    val events = remember { mutableStateOf(listOf<Event>()) }


    // fetch events from Database
    LaunchedEffect(Unit) {
        loadEventsFromSupabase(events)
    }

    NavHost(
        navController = navController,
        startDestination = "map_screen"
    ) {
        composable("map_screen") {
            MapScreen(
                eventsState = events,
                onEventClick = { event ->
                    navController.navigate("event_data_screen/${event.id}")
                }
            )
        }
        composable(
            "event_data_screen/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId")
            val event = events.value.firstOrNull { it.id == eventId }
            event?.let {
                EventDataScreen(
                    event = it,
                    paddingValues = PaddingValues(),
                    onBack = { navController.popBackStack() },
                    goAlone = {navController.navigate("goAlone/${event.id}")},
                    addToFavorites = {}//,
               //     goCar ={navController.navigate("carListScreen/${event}")}
                )
            }
        }
    }
}
