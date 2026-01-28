package com.example.willgo.view.screens

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.willgo.data.Event
import java.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.willgo.view.screens.navScreens.TopBar
import com.example.willgo.view.screens.navScreens.getWillgoForUser
import java.text.ParseException
import java.util.*
import com.example.willgo.view.sections.CommonEventCard

@Composable
fun CalendarScreen(
    userNickname: String,
    navController: NavController,
    paddingValues: PaddingValues
) {

    var selectedDate by remember{ mutableStateOf(Calendar.getInstance()) }
    val eventsByDate = remember { mutableStateOf<Map<String, List<Event>>>(emptyMap()) }
    val userEvents = remember { mutableStateOf<List<Event>>(emptyList()) }
    var viewMode by remember { mutableStateOf("Semana") }

    var weekDays by remember {
        mutableStateOf(
            if (viewMode == "Semana") getWeekDays(selectedDate, viewMode)
            else getMonthDays(selectedDate)
        )
    }

    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    LaunchedEffect(userNickname) {
        userEvents.value = getWillgoForUser(userNickname)
        eventsByDate.value = groupEventsByDate(userEvents.value)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
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

        // Encabezado Mes y Vista Seleccionable
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Flecha izquierda
            IconButton(onClick = {
                if(viewMode == "Semana"){
                    selectedDate.add(Calendar.WEEK_OF_YEAR, -1)
                    weekDays = getWeekDays(selectedDate, viewMode)
                } else{
                    selectedDate.add(Calendar.MONTH, -1)
                    weekDays = getMonthDays(selectedDate)
                }
            }) {
                Icon(Icons.Default.ArrowCircleLeft, contentDescription = "Anterior")
            }

            // Texto de mes clicable
            Box {
                Text(
                    text = SimpleDateFormat("MMMM", Locale.getDefault()).format(selectedDate.time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { expandedMonth = true }
                )
                DropdownMenu(
                    expanded = expandedMonth,
                    onDismissRequest = { expandedMonth = false }
                ) {
                    val months = listOf(
                        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                    )
                    months.forEachIndexed { index, month ->
                        DropdownMenuItem(text = { Text(month) }, onClick = {
                            selectedDate.set(Calendar.MONTH, index)
                            weekDays = if (viewMode == "Semana") {
                                        getWeekDays(selectedDate, viewMode)
                                        } else{
                                            getMonthDays(selectedDate)
                                        }
                            expandedMonth = false
                        })
                    }
                }
            }

            // Texto de año clicable
            Box {
                Text(
                    text = SimpleDateFormat("yyyy", Locale.getDefault()).format(selectedDate.time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { expandedYear = true }
                )
                DropdownMenu(
                    expanded = expandedYear,
                    onDismissRequest = { expandedYear = false }
                ) {
                    val currentYear = selectedDate.get(Calendar.YEAR)
                    val years = (2024 ..2050).toList()
                    years.forEach { year ->
                        DropdownMenuItem(text = { Text(year.toString()) }, onClick = {
                            selectedDate.set(Calendar.YEAR, year)
                            weekDays = if (viewMode == "Semana"){
                                            getWeekDays(selectedDate, viewMode)
                                        } else {
                                            getMonthDays(selectedDate)
                                        }
                            expandedYear = false
                        })
                    }
                }
            }

            //Flecha derecha
            IconButton(onClick = {
                if (viewMode == "Semana"){
                    selectedDate.add(Calendar.WEEK_OF_YEAR, 1)
                    weekDays = getWeekDays(selectedDate, viewMode)
                } else{
                    selectedDate.add(Calendar.MONTH, 1)
                    weekDays = getMonthDays(selectedDate)
                }
            }) {
                Icon(Icons.Default.ArrowCircleRight, contentDescription = "Siguiente")
            }
        }

        // Botón de Vista Completa o Semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ToggleButton(viewMode) { mode ->
                viewMode = mode
                weekDays = if (mode == "Semana") getWeekDays(selectedDate, mode)
                else getMonthDays(selectedDate)
            }
        }

        // Vista de Semana o Mes
        if (viewMode == "Semana") {
            WeekDaysRow(weekDays, selectedDate) { newDate ->
                selectedDate = newDate
            }
        } else {
            MonthView( weekDays,selectedDate) { newDate ->
                selectedDate = newDate
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de Eventos
        Text(
            text = "Próximas actividades",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        EventList(selectedDate, eventsByDate.value, navController)
    }
}

@Composable
fun MonthView(monthDays: List<Calendar>,selectedDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
        items(monthDays.size) { index ->
            val date = monthDays[index]
            val isSelected = isSameDay(date, selectedDate)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.get(Calendar.DAY_OF_MONTH).toString(),
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getMonthDays(baseDate: Calendar): List<Calendar> {
    val result = mutableListOf<Calendar>()
    val firstDayOfMonth = baseDate.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

    repeat(daysInMonth) {
        result.add(firstDayOfMonth.clone() as Calendar)
        firstDayOfMonth.add(Calendar.DAY_OF_MONTH, 1)
    }
    return result
}

@Composable
fun ToggleButton(viewMode: String, onModeChange: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = CircleShape)
    ) {
        val modes = listOf("Mes completo", "Semana")
        modes.forEach { mode ->
            Text(
                text = mode,
                color = if (mode == viewMode) Color.White else Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        if (mode == viewMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onModeChange(mode) },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WeekDaysRow(weekDays: List<Calendar>, selectedDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(weekDays.size) { index ->
            val date = weekDays[index]
            val isSelected = isSameDay(date, selectedDate)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onDateSelected(date) }
            ) {
                Text(
                    text = SimpleDateFormat("E", Locale.getDefault()).format(date.time),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.get(Calendar.DAY_OF_MONTH).toString(),
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EventList(
    selectedDate: Calendar, // Fecha seleccionada
    eventsByDate: Map<String, List<Event>>, // Eventos agrupados por fecha
    navController: NavController // Controlador de navegación
) {
    // Formateador para convertir la fecha seleccionada en una clave para el mapa
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val selectedDateKey = dateFormatter.format(selectedDate.time) // Clave para la fecha seleccionada
    val eventsForSelectedDate = eventsByDate[selectedDateKey] ?: emptyList() // Obtiene los eventos para esa fecha

    Log.d("EventListDebug", "Selected Date Key: $selectedDateKey, Events: $eventsForSelectedDate")

    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre la vista de días y los eventos

    if (eventsForSelectedDate.isEmpty()) {
        // Si no hay eventos para la fecha seleccionada, muestra un mensaje
        Text(
            text = "No hay eventos para esta fecha.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp),
            color = Color.Gray
        )
    } else {
        // Si hay eventos, los muestra en una columna
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), // Espaciado entre eventos
            modifier = Modifier.fillMaxWidth()
        ) {
            // Recorre cada evento y lo muestra como una tarjeta clicable
            items(eventsForSelectedDate.size) { index ->
                val event = eventsForSelectedDate[index]
                CommonEventCard(
                    event = event,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("eventDetail/${event.id}") // Navega a los detalles del evento
                        }
                        .padding(vertical = 8.dp) // Espaciado entre tarjetas
                )
            }
        }
    }
}

fun groupEventsByDate(events: List<Event>): Map<String, List<Event>> {
    val inputDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato esperado de entrada
    val outputDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato esperado de salida

    events.forEach { event ->
        Log.d("EventDebug", "Event: ${event.name_event}, Date: ${event.date}")
    }

    return events.groupBy { event ->
        try {
            event.date?.let {
                // Intenta analizar y reformatear la fecha
                val parsedDate = inputDateFormatter.parse(it)
                outputDateFormatter.format(parsedDate) // Devuelve la fecha formateada
            } ?: "" // Maneja el caso de fecha nula
        } catch (e: ParseException) {
            Log.e("DateParsing", "Error parsing date: ${event.date}. Expected format: dd/MM/yyyy", e)
            "" // Usa una clave vacía si hay un error
        }
    }.filterKeys { it.isNotEmpty() } // Filtra claves vacías
}

fun getWeekDays(baseDate: Calendar, viewMode: String): List<Calendar> {
    val result = mutableListOf<Calendar>()
    val startOfWeek = baseDate.clone() as Calendar
    startOfWeek.firstDayOfWeek = Calendar.MONDAY
    startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val days = if (viewMode == "Semana") 7 else 30
    repeat(days) {
        result.add(startOfWeek.clone() as Calendar)
        startOfWeek.add(Calendar.DAY_OF_MONTH, 1)
    }
    return result
}

fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
            date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
}

