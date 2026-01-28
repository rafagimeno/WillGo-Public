package com.example.willgo.view.screens.other

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.willgo.data.Category
import com.example.willgo.data.Event
import com.example.willgo.view.sections.CommonEventCard

@Composable
fun CategoryScreen(onBack: () -> Unit, category: Category, events: List<Event>, navController: NavHostController, paddingValuesNav: PaddingValues){
    Scaffold(
        topBar = {TopBar(category.toString(), onBack)},
    ){
        CategorySection(events.filter { it.category == category }, it, navController, paddingValuesNav)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(categoryName: String, onBack: () -> Unit){
    TopAppBar(title = {  Text(categoryName.uppercase().replace("_", " "))},
        navigationIcon = {
            IconButton(onClick = onBack){ Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ArrowBack")}
        }
    )
}

@Composable
fun CategorySection(events: List<Event>, paddingValues: PaddingValues, navController: NavHostController, paddingValuesNav: PaddingValues){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding(), bottom = paddingValuesNav.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        items(count = events.size){
            event -> CommonEventCard(events[event], Modifier.clickable {navController.navigate("eventDetail/${events[event].id}")})
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/*
@Preview
@Composable
fun EventSectionPreview(){
    val navController = NavHostController(LocalContext.current)
    val event = Event(
        id = 1,
        description = "¡No te pierdas el concierto del año! AC/DC regresa a la escena.",
        name_event = "Concierto AC/DC 2025",
        email_contact = "contacto@acdc2025.com",
        phone = 123456789,
        category = Category.Actuacion_musical,
        location = "Estadio Nacional, Ciudad de México",
        date = "2025-06-15",
        price = 150.0f,
        image = "https://trpgyhwsghxnaakpoftt.supabase.co/storage/v1/object/public/EventImage/ac_dc.jpg"
    )
    val events = listOf(event, event, event, event, event)
    CategoryScreen(category = Category.Actuacion_musical, onBack = {}, events = events, navController = navController)
}
 */