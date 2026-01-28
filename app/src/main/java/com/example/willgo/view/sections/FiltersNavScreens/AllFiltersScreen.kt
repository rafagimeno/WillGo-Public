package com.example.willgo.view.sections.FiltersNavScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.willgo.data.Category
import com.example.willgo.data.Event
import com.example.willgo.graphs.FiltersScreen
import com.example.willgo.graphs.buildSearchRoute
import com.example.willgo.view.sections.FilterRow
import com.example.willgo.view.sections.FiltersTagView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllFilters(navController: NavController, navControllerMain: NavController, events: List<Event>) {
    // Estado para almacenar las selecciones
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedPrice by remember { mutableStateOf("Todos") }
    var selectedType by remember { mutableStateOf("Todos") }
    var selectedDate by remember { mutableStateOf("Todos") }
    var queryParam by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtros") }
            )
        },
        bottomBar = {
            ResultFilterButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
                onClick = {
                    val maxPriceParam = when (selectedPrice) {
                        "Todos" -> 10000f
                        "Gratis" -> 0f
                        else -> selectedPrice.removeSuffix(" euros").toFloatOrNull() ?: 10000f
                    }
                    val route = buildSearchRoute(
                        query = queryParam.takeIf { it.isNotEmpty() },
                        maxPrice = maxPriceParam.takeIf { it != 10000f },
                        category = selectedCategory,
                        type = selectedType.takeIf { it != "Todos" },
                        date = selectedDate.takeIf { it != "Todos" }
                    )
                    navControllerMain.navigate(route)
                }

            )
        }
    ) {

        Column(modifier = Modifier.padding(it)) {

            // Llamada a FiltersTagView para mostrar los filtros seleccionados
            FiltersTagView(
                selectedCategory = selectedCategory,
                selectedPrice = selectedPrice,
                selectedType = selectedType,
                selectedDate = selectedDate,
                onRemoveCategory = { selectedCategory = null },
                onRemovePrice = { selectedPrice = "Todos" },
                onRemoveType = { selectedType = "Todos" },
                onRemoveDate = { selectedDate = "Todos" },
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Filtro de categoria
            FilterRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        // Navegar a `CategoriesNavScreen` y actualizar la selección de categoría
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedCategory", selectedCategory)
                        navController.navigate(FiltersScreen.Categories.route)
                    },
                filterName = "Categoria",
                value = selectedCategory?.name ?: "Todos"
            )

            //Filtro de precio
            FilterRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        // Navegar a `PriceNavScreen` y actualizar la selección de precio
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedPrice", selectedPrice)
                        navController.navigate(FiltersScreen.Price.route)
                    },
                filterName = "Precio",
                value = selectedPrice
            )

            //Filtro de tipo de lugar
            FilterRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedType", selectedType)
                        navController.navigate(FiltersScreen.Type.route)
                    },
                filterName = "Tipo de lugar",
                value = selectedType
            )

            //Filtro de fecha
            FilterRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        navController.navigate(FiltersScreen.Date.route)
                },
                filterName = "Fecha",
                value = selectedDate
            )
        }
    }

    // Recuperar las selecciones al regresar de las pantallas de selección
    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Category>("selectedCategory")
        ?.observe(navController.currentBackStackEntry!!) { selectedCategory = it }

    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedPrice")
        ?.observe(navController.currentBackStackEntry!!) { selectedPrice = it }

    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedType")
        ?.observe(navController.currentBackStackEntry!!) { selectedType = it }

    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedDate")
        ?.observe(navController.currentBackStackEntry!!) { selectedDate = it }
}

@Composable
fun ResultFilterButton(modifier: Modifier, onClick: () -> Unit){
    Box(modifier = modifier){
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Text(text = "Aplicar filtros")
        }
    }
}