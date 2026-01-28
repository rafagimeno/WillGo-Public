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
fun TypeNavScreen(
    onBack: () -> Unit,
    modifier: Modifier,
    navController: NavController,
    onTypeSelected: (String) -> Unit
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
            Text("Tipo de lugar")
            Spacer(modifier = Modifier.height(8.dp))

            FilterValueRow(modifier = modifier, value = "Todos", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedType", "Todos")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "Interior", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedType", "Interior")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "Exterior", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedType", "Exterior")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "Online", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedType", "Online")
                navController.popBackStack()
            })
        }
    }
}