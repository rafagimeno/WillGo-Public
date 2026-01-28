package com.example.willgo.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.willgo.data.Category
import com.example.willgo.data.Event
import com.example.willgo.view.screens.navScreens.SearchBar
import com.example.willgo.view.screens.navScreens.TopBar
import com.example.willgo.view.sections.CommonEventCard
import com.example.willgo.view.sections.FiltersTagViewSearchScreen
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    paddingValues: PaddingValues,
    events: List<Event>,
    initialQuery: String,
    initialCategory: Category? = null,
    maxPrice: Float? = null,
    externalSelectedCategory: Category? = null,
    typeFilter: String? = null,
    dateFilter: String? = null,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    navController: NavController
) {

    var query by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedPrice by remember { mutableStateOf(maxPrice?.toString() ?: "Todos") }
    var selectedType by remember { mutableStateOf(typeFilter ?: "Todos") }
    var selectedDate by remember { mutableStateOf(dateFilter ?: "Todos") }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Obtiene la fecha actual
    val today = dateFormatter.parse(dateFormatter.format(Calendar.getInstance().time)) ?: Calendar.getInstance().time

    // Calcula los límites de la semana y el mes actuales
    val calendar = Calendar.getInstance()

    // Calcular los límites de la semana actual (desde el primer día de la semana hasta el último)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    val startOfWeek = dateFormatter.parse(dateFormatter.format(calendar.time)) ?: today
    calendar.add(Calendar.DAY_OF_WEEK, 6)
    val endOfWeek = dateFormatter.parse(dateFormatter.format(calendar.time)) ?: today

    // Calcular los límites del mes actual (desde el primer día hasta el último)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startOfMonth = dateFormatter.parse(dateFormatter.format(calendar.time)) ?: today
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endOfMonth = dateFormatter.parse(dateFormatter.format(calendar.time)) ?: today

    var categoryToFilter by remember { mutableStateOf(externalSelectedCategory ?: selectedCategory) }


    val filteredEvents by remember {
        derivedStateOf {
            events.filter { event ->
                val eventDate = try {
                    dateFormatter.parse(event.date ?: "")
                } catch (e: Exception) {
                    null
                }

                eventDate != null &&
                        (categoryToFilter == null || event.category == categoryToFilter) &&
                        (selectedPrice == "Todos" || (event.price ?: 0f) <= (selectedPrice.toFloatOrNull() ?: 0f)) &&
                        (selectedType == "Todos" || event.type.equals(selectedType, ignoreCase = true)) &&
                        (selectedDate == "Todos" ||
                                (selectedDate == "Hoy" && eventDate.compareTo(today) == 0) ||
                                (selectedDate == "Esta semana" && eventDate in startOfWeek..endOfWeek) ||
                                (selectedDate == "Este mes" && eventDate in startOfMonth..endOfMonth) ||
                                (selectedDate != "Hoy" && selectedDate != "Esta semana" && selectedDate != "Este mes" && event.date == selectedDate)
                                ) &&
                        event.name_event.contains(query, ignoreCase = true)
            }
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
            .background(Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
        ) {
            TopBar(navigationIcon = {
                IconButton(
                    onClick = {
                        //navController.navigate(BottomBarScreen.Home.route)
                        navController.navigate("home") {
                            // Establece `launchSingleTop` para evitar duplicados
                            launchSingleTop = true
                            // Establece `popUpTo` para limpiar el historial hasta `HomeScreen`
                            popUpTo("home") { inclusive = true }
                        }
                    })
                {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ArrowBack"
                    )
                }
            })
            SearchBar(
                text = query,
                events = events,
                onQueryChange = { newQuery ->
                    query = newQuery
                    selectedCategory = null
                },
                onSearch = {
                    onSearch(query)
                },
                navController = navController
            )
            Spacer(modifier = Modifier.height(16.dp))


            FiltersTagViewSearchScreen(
                sheetState = bottomSheetState,
                coroutineScope = coroutineScope,
                events = events,
                navControllerMain = navController,
                selectedCategory = categoryToFilter,
                selectedPrice = selectedPrice,
                selectedType = selectedType,
                selectedDate = selectedDate,
                onRemoveCategory = { categoryToFilter = null },
                onRemovePrice = { selectedPrice = "Todos" },
                onRemoveType = { selectedType = "Todos" },
                onRemoveDate = { selectedDate = "Todos" }
            )

            Text(
                text = "Resultados de la búsqueda:",
                color = Color.Black,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 14.dp, start = 8.dp),
                fontSize = 16.sp
            )

            if (filteredEvents.isEmpty()) {
                Text("No se encontraron eventos.")
            } else {
                LazyColumn(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    items(filteredEvents) { event ->
                        CommonEventCard(event = event, modifier = Modifier.clickable {navController.navigate("eventDetail/${event.id}")})  // Mostrar tarjeta de evento
                    }
                    item{}
                }
            }
        }
    }
}

fun normalizeText(text: String): String {
    return Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace("\\p{M}".toRegex(), "")  // Elimina diacríticos (acentos)
        .replace("[^\\p{ASCII}]".toRegex(), "") // Elimina caracteres no ASCII
        .lowercase()  // Convierte el texto a minúsculas
}