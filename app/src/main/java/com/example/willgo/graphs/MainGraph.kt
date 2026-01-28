package com.example.willgo.graphs

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.willgo.data.Category
import com.example.willgo.data.Event
import com.example.willgo.data.FavoriteEvent
import com.example.willgo.data.User.User
import com.example.willgo.view.screens.CalendarScreen
import com.example.willgo.view.screens.EventDataScreen
import com.example.willgo.view.screens.SearchResultsScreen
import com.example.willgo.view.screens.navScreens.HomeScreen
import com.example.willgo.view.screens.navScreens.MapScreen
import com.example.willgo.view.screens.navScreens.ProfileScreen
import com.example.willgo.view.screens.other.CategoryScreen
import com.example.willgo.view.screens.CommentsOnEvents
import com.example.willgo.view.screens.FollowerScreen
import com.example.willgo.view.screens.CarListScreen
import com.example.willgo.view.screens.AddCarScreen
import com.example.willgo.view.screens.FollowingScreen
import com.example.willgo.view.screens.getUser
import com.example.willgo.view.screens.other.WillGoManagerScreen
import com.example.willgo.view.screens.other.WillGoScreen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.events.Events

@Composable
fun MainNavGraph(navController: NavHostController, paddingValues: PaddingValues, user: User) {
    val events = remember { mutableStateOf(listOf<Event>()) }
    LaunchedEffect(Unit) {
        loadEventsFromSupabase(events)
    }

    // Estado para almacenar la categoría seleccionada externamente
    val externalSelectedCategory = remember { mutableStateOf<Category?>(null) }

    NavHost(navController = navController, startDestination = BottomBarScreen.Home.route, route = Graph.MAIN) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen(paddingValues = paddingValues, events.value, navController, user.name)
        }

        composable(route = BottomBarScreen.Location.route) {
            MapScreen(
                eventsState = events,
                onEventClick = { event ->
                    navController.navigate("eventDetail/${event.id}")
                })
        }

        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen(navController = navController, paddingValues = paddingValues, user = user,false)
        }

        //Ruta para acceder al calendario
        composable(route = "calendar") {
            CalendarScreen(userNickname = user.nickname, navController = navController, paddingValues = paddingValues)
        }

        composable(
            route = "searchResults?query={query}&maxPrice={maxPrice}&category={category}&type={type}&date={date}",
            arguments = listOf(
                navArgument("query") { defaultValue = "" },
                navArgument("maxPrice") { defaultValue = "10000" },
                navArgument("category") { defaultValue = "" },
                navArgument("type") { defaultValue = "Todos" },
                navArgument("date") { defaultValue = "Todos" },
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val maxPrice = backStackEntry.arguments?.getString("maxPrice")?.toFloatOrNull() ?: 10000f
            //val category = backStackEntry.arguments?.getString("category")?.let { Category.valueOf(it) }
            val category = backStackEntry.arguments?.getString("category")?.takeIf { it.isNotEmpty() }?.let { Category.valueOf(it) }
            val typeFilter = backStackEntry.arguments?.getString("type")
            val dateFilter = backStackEntry.arguments?.getString("date")

            SearchResultsScreen(
                paddingValues = paddingValues,
                events = events.value,
                initialQuery = query,
                maxPrice = if (maxPrice == 10000f) null else maxPrice,
                externalSelectedCategory = category,
                typeFilter = if (typeFilter == "Todos") null else typeFilter,
                dateFilter = if (dateFilter == "Todos") null else dateFilter,
                onQueryChange = { newQuery ->
                    navController.navigate(
                        buildSearchRoute(
                            query = newQuery,
                            maxPrice = maxPrice,
                            category = category,
                            type = typeFilter,
                            date = dateFilter)
                    )
                },
                onSearch = { searchQuery ->
                    navController.navigate(
                        buildSearchRoute(
                            query = searchQuery,
                            maxPrice = maxPrice,
                            category = category,
                            type = typeFilter,
                            date = dateFilter)
                    )
                },
                navController = navController
            )
        }

        composable(route = "searchResults/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val filteredEvents = events.value.filter { it.name_event.contains(query, ignoreCase = true) } // Filtrar eventos
            SearchResultsScreen(
                paddingValues,
                events = filteredEvents,
                initialQuery = query,
                initialCategory = null,
                maxPrice = null,
                externalSelectedCategory = externalSelectedCategory.value,
                typeFilter = null,
                dateFilter = null,
                onQueryChange = { newQuery ->
                    navController.navigate("searchResults/$newQuery")
                },
                onSearch = { searchQuery ->
                    navController.navigate("searchResults/$searchQuery")
                },
                navController
            )
        }

        //ruta para buscar por categoria y por nombre
        composable(route = "searchResults/{query}/{category}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val category = backStackEntry.arguments?.getString("category")?.let { Category.valueOf(it) }
            val filteredEvents = events.value.filter { it.name_event.contains(query, ignoreCase = true) && it.category == category } // Filtrar eventos
            SearchResultsScreen(
                paddingValues,
                events = filteredEvents,
                initialQuery = query,
                initialCategory = category,
                maxPrice = null,
                externalSelectedCategory = externalSelectedCategory.value,
                typeFilter = null,
                dateFilter = null,
                onQueryChange = { newQuery ->
                    navController.navigate("searchResults/$newQuery/${category}")
                },
                onSearch = { searchQuery ->
                    navController.navigate("searchResults/$searchQuery/${category}")
                },
                navController
            )
        }

        composable(
            route = "eventDetail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val event = backStackEntry.arguments?.getInt("eventId") ?: -1
            val filteredEvents = events.value.filter { it.id.toInt() == event }
            EventDataScreen(filteredEvents[0], paddingValues, onBack = { navController.popBackStack() },
                goAlone ={navController.navigate("goAlone/${filteredEvents[0].id}")},
                addToFavorites = {})
        }

        composable(
            route = "goAlone/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val event = backStackEntry.arguments?.getInt("eventId") ?: -1
            val filteredEvents = events.value.filter { it.id.toInt() == event }
            WillGoScreen(
                filteredEvents[0].id,
                paddingValues,
                onBack = { navController.popBackStack() },
                navHostController = navController,
                goCar ={navController.navigate("carListScreen/${event}")}
            )
        }


        composable(
            route = "comments/{nickname}",
            arguments = listOf(navArgument("nickname") { type = NavType.StringType })
        ) { backStackEntry ->
            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
            CommentsOnEvents(
                navController = navController,
                nickname = nickname,
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "profile/{nickname}",
            arguments = listOf(navArgument("nickname") { type = NavType.StringType })
        ) { backStackEntry ->
            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
            val userState = remember { mutableStateOf<User?>(null) }

            LaunchedEffect(nickname) {
                userState.value = getUser(nickname)
            }

            // Mientras `user` es nulo, puedes mostrar una pantalla de carga o un placeholder
            if (userState.value != null) {
                ProfileScreen(
                    navController = navController,
                    paddingValues = paddingValues,
                    user = userState.value!!,
                    showBackArrow = true
                )
            }
        }

        composable(
            route = "following/{nickname}",
            arguments = listOf(navArgument("nickname") { type = NavType.StringType })
        ) { backStackEntry ->
            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
            FollowingScreen(
                navController = navController,
                nickname = nickname,
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "follower/{nickname}"
            //arguments = listOf(navArgument("nickname") { type = NavType.StringType })
        ) { backStackEntry ->
            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
            FollowerScreen(
                navController = navController,
                nickname = nickname,
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "WillGoManager"
        ) {
            WillGoManagerScreen(
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() },
                navHostController = navController
            )
        }

        composable(
            route = "Category_Section/{categoryName}", // Cambiado para aceptar un parámetro de ruta
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "DEFAULT"
            val category = getCategory(categoryName) // Usa el nombre para obtener la categoría correcta
            CategoryScreen(onBack = { navController.popBackStack() }, category = category, events.value, navController, paddingValues)
        }


        composable("carListScreen/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull() ?: 0
            CarListScreen(
                eventId = eventId,
                onAddCarClicked = { navController.navigate("addCarScreen/$eventId") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("addCarScreen/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull() ?: 0
            AddCarScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }

    }
}


fun getCategory(categoryName: String): Category{
     return when(categoryName){
        "Actuacion musical" ->  Category.Actuacion_musical
        "Comedia" ->  Category.Comedia
        "Cultura" ->  Category.Cultura
        "Deporte" ->  Category.Deporte
        "Discoteca" ->  Category.Discoteca
        "Teatro" ->  Category.Teatro
        else ->  Category.Actuacion_musical

    }


}

suspend fun loadEventsFromSupabase(eventsState: MutableState<List<Event>>){

        try{
            val client = getClient()
            val supabaseResponse = client.postgrest["Evento"].select()
            val events = supabaseResponse.decodeList<Event>()
            Log.d("Supabase", "Eventos obtenidos: ${events.size}")
            eventsState.value = events
        } catch (e: Exception) {
            Log.e("Supabase", "Error al obtener eventos: ${e.message}")
        }
    }

private fun getClient(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = "https://trpgyhwsghxnaakpoftt.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRycGd5aHdzZ2h4bmFha3BvZnR0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwMjgwNDcsImV4cCI6MjA0MzYwNDA0N30.IJthecg-DH9rwOob2XE6ANunb6IskxCbMAacducBVPE"
    ){
        install(Postgrest)
    }
}

sealed class HomeScreenRoutes(val route: String){
    object Category: HomeScreenRoutes("Category_Section/{categoryName}")
    object DetailEvent: HomeScreenRoutes("Detail_Event")
}

fun buildSearchRoute(
    query: String? = null,
    maxPrice: Float? = null,
    category: Category? = null,
    type: String? = null,
    date: String? = null
): String {
    return buildString {
        append("searchResults?")
        query?.takeIf { it.isNotEmpty() }?.let { append("query=$it&") }
        maxPrice?.takeIf { it != 10000f }?.let { append("maxPrice=$it&") }
        category?.let { append("category=${it.name}&") }
        type?.takeIf { it != "Todos" }?.let { append("type=$it&") }
        date?.takeIf { it != "Todos" }?.let { append("date=$it&") }
    }.removeSuffix("&")
}

suspend fun getFavoriteEventIds(): List<Long> {
    val client = getClient()
    return try {
        client.postgrest["Eventos_favoritos"]
            .select()
            .decodeList<FavoriteEvent>()
            .map { it.event_id }
    } catch (e: Exception) {
        Log.e("getFavoriteEventIds", "Error retrieving favorite event IDs: ${e.message}")
        emptyList()
    }
}