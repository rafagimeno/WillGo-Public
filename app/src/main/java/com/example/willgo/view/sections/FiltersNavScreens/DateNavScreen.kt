package com.example.willgo.view.sections.FiltersNavScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.willgo.view.sections.FilterValueRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateNavScreen(
    onBack: () -> Unit,
    modifier: Modifier,
    navController: NavController,
    onDateSelected: (String) -> Unit
){
    val context = LocalContext.current
    val state = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtros") },
                navigationIcon = { IconButton(onClick = onBack){ Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null) } }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text("Fecha")
            Spacer(modifier = Modifier.height(8.dp))

            FilterValueRow(modifier = modifier, value = "Hoy", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedDate", "Hoy")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "Esta semana", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedDate", "Esta semana")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "Este mes", onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("selectedDate", "Este mes")
                navController.popBackStack()
            })
            FilterValueRow(modifier = modifier, value = "Personalizado", onClick = {
                showDialog = true
            })

            // DatePickerDialog para seleccionar una fecha específica
            if (showDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            val selectedDate = Date(state.selectedDateMillis ?: System.currentTimeMillis())
                            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
                            navController.previousBackStackEntry?.savedStateHandle?.set("selectedDate", formattedDate)
                            showDialog = false
                            navController.popBackStack()
                        }) { Text("Confirmar") }
                    }
                ) {
                    DatePicker(state = state)
                }
            }
        }
    }
}