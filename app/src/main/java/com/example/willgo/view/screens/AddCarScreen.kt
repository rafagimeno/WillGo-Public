package com.example.willgo.view.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.willgo.data.SharedCar
import kotlinx.coroutines.launch
import com.example.willgo.view.screens.CarListScreen
import io.github.jan.supabase.postgrest.postgrest
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(eventId: Int, onBack: () -> Unit,) {
    var id by remember { mutableStateOf("") }
    var user_nickname by remember { mutableStateOf("") }
    var seats_available by remember { mutableStateOf("") }
    var departure_time by remember { mutableStateOf("") }
    var departure_address by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var openDatePicker by remember { mutableStateOf(false) }
    var openTimePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    if (openDatePicker) {
        DatePickerDialog(
            LocalContext.current,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                openTimePicker = true
                openDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (openTimePicker) {
        TimePickerDialog(
            LocalContext.current,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Formato de fecha y hora
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                departure_time = dateFormat.format(calendar.time)
                openTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }







    Column(modifier = Modifier.padding(16.dp)) {



        TopAppBar(
            title = { Text("Añadir coche compartido") },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            }
        )
        // Campos del formulario
        OutlinedTextField(
            value = seats_available,
            onValueChange = { seats_available = it },
            label = { Text("Plazas disponibles") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = seats_available.isNotEmpty() && seats_available.toIntOrNull() == null // Validación
        )
        if (seats_available.isNotEmpty() && seats_available.toIntOrNull() == null) {
            Text("Por favor ingresa un número válido")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = departure_time,
            onValueChange = {},
            label = { Text("Hora de salida") },
            readOnly = true, // No permitir que el usuario edite directamente
            trailingIcon = {
                IconButton(onClick = { openDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Select Date and Time")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = departure_address,
            onValueChange = { departure_address = it },
            label = { Text("Dirección de salida") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar el coche
        Button(onClick = {
            if (seats_available.isNotEmpty() && departure_time.isNotEmpty() && departure_address.isNotEmpty()) {
                coroutineScope.launch {
                    addCarToDatabase(
                        event_id = eventId,
                        user_nickname = user_nickname,
                        seats_available = seats_available.toInt(),
                        departure_time = departure_time,
                        departure_address = departure_address,
                        onSuccess = {
                            println("Coche añadido correctamente")
                            onBack() // Vuelve a la pantalla anterior
                        }
                    )

                }
            }
        }) {
            Text("Añadir coche")
        }
    }
}



suspend fun addCarToDatabase(
    event_id: Int,
    user_nickname: String,
    seats_available: Int,
    departure_time: String,
    departure_address: String,
    onSuccess: () -> Unit
) {
    val user = getUser() // Obtener el usuario actual
    val client = getClient() // Obtener el cliente de Supabase
    Log.d("TECUENTOOOOOOOO","nickname = ${user.nickname}")

    // Crear el objeto SharedCar sin incluir el ID
    val car = SharedCar(
        event_id = event_id,
        user_nickname = user.nickname ?: "",
        seats_available = seats_available,
        departure_time = departure_time,
        departure_address = departure_address
    )

    try {
        // Insertar el nuevo coche en la base de datos
        Log.d("OOOOOOOOOOOOOOOOOO", "Datos del coche: eventId=$event_id, userId=${user.nickname}, seatsAvailable=$seats_available, departureTime=$departure_time, departureAddress=$departure_address")
        client.postgrest["Coche_compartido"].insert(car)
        onSuccess() // Llamar a la función de éxito para actualizar la UI
    } catch (e: Exception) {
        Log.e("addCarToDatabase", "Error al añadir el coche compartido: $e")
    }
}


@Composable
fun DateTimePickerField(
    label: String,
    selectedDateTime: String,
    onDateTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var openDatePicker by remember { mutableStateOf(false) }
    var openTimePicker by remember { mutableStateOf(false) }

    // Mostrar DatePicker cuando el usuario toca el campo
    if (openDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Fecha seleccionada
                calendar.set(year, month, dayOfMonth)

                // Abrir TimePicker después de seleccionar la fecha
                openTimePicker = true
                openDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Mostrar TimePicker cuando el usuario selecciona la fecha
    if (openTimePicker) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Establecer la hora seleccionada
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Formatear la fecha y hora seleccionada en el formato YYYY/MM/dd HH:mm:ss
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                val formattedDateTime = dateFormat.format(calendar.time)

                // Llamar al callback con el valor formateado
                onDateTimeSelected(formattedDateTime)
                openTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Cuadro de texto de solo lectura con el valor de la fecha y hora seleccionados
    OutlinedTextField(
        value = selectedDateTime,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true, // No permitir la edición directa
        trailingIcon = {
            IconButton(onClick = { openDatePicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date and Time")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
    )
}

