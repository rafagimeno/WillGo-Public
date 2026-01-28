package com.example.willgo.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.willgo.data.Category
import com.example.willgo.data.Event
import com.example.willgo.view.screens.getUser
import com.example.willgo.view.screens.navScreens.HomeScreen
import com.example.willgo.view.sections.FiltersNavScreens.AllFilters
import com.example.willgo.view.sections.FiltersNavScreens.CategoriesNavScreen
import com.example.willgo.view.sections.FiltersNavScreens.DateNavScreen
import com.example.willgo.view.sections.FiltersNavScreens.PriceNavScreen
import com.example.willgo.view.sections.FiltersNavScreens.TypeNavScreen

sealed class FiltersScreen(val route: String){
    object Filters: FiltersScreen("filters_screen")
    object Categories: FiltersScreen("categories_screen")
    object Date: FiltersScreen("date_screen")
    object Hour: FiltersScreen("hour_screen")
    object Distance: FiltersScreen("distance_screen")
    object Price: FiltersScreen("price_screen")
    object Type: FiltersScreen("type_screen")
}

@Composable
fun FiltersNavGraph(navController: NavHostController, events: List<Event>, paddingValues: PaddingValues, navControllerMain: NavController){
    val onBack: () -> Unit = { navController.popBackStack() }
    val modifier = Modifier.fillMaxWidth().height(56.dp)

    val user = remember {mutableStateOf("")}
    LaunchedEffect(Unit) {
        user.value = getUser().name


    }
    val externalSelectedCategory = remember { mutableStateOf<Category?>(null) }
    val maxPriceFilter = remember { mutableStateOf<Float?>(null) }
    val selectedTypeFilter = remember { mutableStateOf<String?>(null) }
    val selectedDateFilter = remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = FiltersScreen.Filters.route, route = Graph.MAIN) {
        composable(route = FiltersScreen.Filters.route) {
            AllFilters(navController, navControllerMain, events)
        }

        composable(route = FiltersScreen.Categories.route) {
            CategoriesNavScreen(
                onBack = onBack,
                modifier = modifier,
                onCategorySelected = { selectedCategory ->
                    externalSelectedCategory.value = selectedCategory
                    // Navega a SearchResultsScreen pasando la categoría seleccionada
                    navController.navigate("searchResults?externalSelectedCategory=${selectedCategory.name}")
                },
                navController = navController
            )
        }

        composable(route = FiltersScreen.Date.route){
            DateNavScreen(
                onBack = onBack,
                modifier,
                navController = navController,
                onDateSelected = { selectedDate ->
                    selectedDateFilter.value = selectedDate
                }
            )
        }

        composable(route = FiltersScreen.Price.route) {
            PriceNavScreen(
                onBack = onBack,
                modifier,
                navController,
                onPriceSelected = { maxPrice ->
                    maxPriceFilter.value = maxPrice
                }
            )
        }

        composable(route = FiltersScreen.Type.route) {
            TypeNavScreen(
                onBack = onBack,
                modifier,
                navController,
                onTypeSelected = { selectedType ->
                    selectedTypeFilter.value = selectedType
                }
            )
        }

        composable(route = "home") {
            HomeScreen(paddingValues = paddingValues, events, navController, user.value)
        }
    }
}
