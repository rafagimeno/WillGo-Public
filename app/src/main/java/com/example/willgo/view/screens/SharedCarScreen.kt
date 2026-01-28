package com.example.willgo.view.screens

import android.annotation.SuppressLint

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest


import io.github.jan.supabase.postgrest.*
import android.util.Log
import androidx.compose.foundation.layout.*    // Para los espacios y layouts
import androidx.compose.foundation.lazy.LazyColumn  // Para listas optimizadas
import androidx.compose.foundation.lazy.items       // Para iterar sobre listas en Compose
import androidx.compose.material.*            // Para componentes de Material Design como Card, Button, etc.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*             // Para estados como `remember` y `LaunchedEffect`
import androidx.compose.ui.Modifier           // Para modificaciones de tamaño y layout
import androidx.compose.ui.unit.dp            // Para definir márgenes, paddings, etc.
import com.example.willgo.data.SharedCar
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import com.example.willgo.data.Comment
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.Toast
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.willgo.data.User.User


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(eventId: Int, onAddCarClicked: () -> Unit, onBack: () -> Unit) {
    val client = getClient()
    var user by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        user = getUser().nickname
    }
    var carList by remember { mutableStateOf(listOf<SharedCar>()) }
    val coroutineScope = rememberCoroutineScope()
    var selectedCarId by remember { mutableStateOf<Int?>(null) } // Coche seleccionado
    var showDialog by remember { mutableStateOf(false) } // Para mostrar la ventana emergente
    var selectedUserNickname by remember { mutableStateOf("") }
    val context = LocalContext.current



    LaunchedEffect(eventId) {

        coroutineScope.launch {
            carList = getCarList(eventId)

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coches disponibles") },
                navigationIcon = {
                    IconButton(onClick = onBack) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { //paddingValues ->
        //Spacer(modifier = Modifier.height(16.dp))
        LazyColumn (Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            items(carList) { sharedCar ->
                var seatsLeft by remember { mutableIntStateOf(sharedCar.seats_available) }
                // var isUserJoined by remember { mutableStateOf(sharedCar.isUserJoined) }
                //val availableSeats = sharedCar.seats_available - sharedCar.usersJoined.size

                Log.d("YUYUYUYUYUYUYUYU", "IsUserJoined = ${sharedCar.isUserJoined}")
                if (seatsLeft > 0 || sharedCar.isUserJoined) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                            //    .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column (modifier = Modifier.padding(10.dp)){
                                Text("Usuario: ${sharedCar.user_nickname}")
                                Text("Plazas disponibles: $seatsLeft")
                                Text("Hora de salida: ${formatDepartureTime(sharedCar.departure_time)}")
                                Text("Dirección: ${sharedCar.departure_address}")
                            }
                            if (user != sharedCar.user_nickname) {
                                Button(
                                    onClick = {
                                        if (sharedCar.isUserJoined) {
                                            seatsLeft += 1
                                            sharedCar.isUserJoined = false
                                            Toast.makeText(
                                                context,
                                                "Te has desapuntado del coche de ${sharedCar.user_nickname}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (seatsLeft > 0) {
                                            seatsLeft -= 1
                                            sharedCar.isUserJoined = true
                                            Toast.makeText(
                                                context,
                                                "Te has añadido al coche de ${sharedCar.user_nickname}!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    enabled = seatsLeft > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (sharedCar.isUserJoined) Color.Green else Color.Blue
                                    )
                                ){
                                    Text(
                                        if (sharedCar.isUserJoined) "Apuntado ✔" else "Apuntarme"
                                    )
                                }
                            }else {
                                //       Spacer(modifier = Modifier.height(16.dp))
                                Text("Tu coche", modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }
            }


            item {

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para añadir un coche
                Button(onClick = onAddCarClicked, modifier = Modifier.padding(1.dp)) {
                    Text("Añadir mi coche")
                }
            }
        }
    }


}



    fun formatDepartureTime(timestamp: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("HH:mm dd/MM", Locale.getDefault())
            formatter.format(parser.parse(timestamp) ?: timestamp)
        } catch (e: Exception) {
            timestamp // En caso de error, devuelve el original
        }
    }


suspend fun apuntarUsuarioACoche(cocheId: Int, userNickname: String) {
    val client = getClient()
    val result = client.postgrest
        .rpc("apuntar_usuario_a_coche", mapOf("coche_id" to cocheId, "user_nickname" to userNickname))
}


suspend fun desapuntarUsuarioDeCoche(cocheId: Int, userNickname: String) {
    val client = getClient()
    val result = client.postgrest
        .rpc("desapuntar_usuario_de_coche", mapOf("coche_id" to cocheId, "user_nickname" to userNickname))
}


suspend fun obtenerUsuariosApuntados(cocheId: Int) {
    val client = getClient()
    val result = client.postgrest
        .rpc("contar_usuarios_apuntados", mapOf("coche_id" to cocheId))
}
