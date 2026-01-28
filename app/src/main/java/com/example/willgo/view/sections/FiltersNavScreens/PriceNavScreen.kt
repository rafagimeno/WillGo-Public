package com.example.willgo.view.sections.FiltersNavScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.willgo.view.sections.FilterValueRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceNavScreen(
    onBack: () -> Unit,
    modifier: Modifier,
    navController: NavController,
    onPriceSelected: (Float) -> Unit
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtros") },
                navigationIcon = { IconButton(onClick = onBack){ Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null) } }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text("Precio")
            Spacer(modifier = Modifier.height(8.dp))
            FilterValueRow(modifier = modifier, value = "Todos", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "Todos")
                navController.popBackStack() // Regresar a AllFiltersScreen
            })
            FilterValueRow(modifier = modifier, value = "Gratis", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "Gratis")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "5 euros", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "5 euros")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "10 euros", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "10 euros")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "20 euros", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "20 euros")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "50 euros", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "50 euros")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "100 euros", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "100 euros")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "200 euros", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedPrice", "200 euros")
                navController.popBackStack()
            })
        }
    }
}