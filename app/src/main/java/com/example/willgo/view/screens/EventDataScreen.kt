package com.example.willgo.view.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.willgo.data.Comment
import com.example.willgo.data.Event
import com.example.willgo.data.FavoriteEvent
import com.example.willgo.data.SharedCar
import com.example.willgo.data.User.User
import com.example.willgo.data.User.UserResponse
import com.example.willgo.data.WillGo.WillGo
import com.example.willgo.view.sections.VerticalSeparator
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EventDataScreen(
    event: Event,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    goAlone: () -> Unit,
    addToFavorites: () -> Unit
) {


    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Secciones
        item { EventHeader(event, onBack, addToFavorites) }
        item { EventDetails(event) }
        item { EventCharacteristics(event) }
        item { EventDescription(event) }
        item { WillGoButtons(event, goAlone) }
        item { EventLocation(event) }
        item { ContactSection(event) }
    }
}


// Encabezado con imagen del evento
@Composable
fun EventHeader(event: Event, onBack: () -> Unit, addToFavorites: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        isFavorite = checkIfFavorite(event)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = event.image,
            contentDescription = "Imagen del evento",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp),
            colors = IconButtonDefaults.iconButtonColors(Color.White)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }
        IconButton(
            onClick = {
                isFavorite = !isFavorite
                coroutineScope.launch {
                    if (isFavorite) {
                        addToFavorite(event)
                    } else {
                        removeFromFavorite(event)
                    }
                }
                      },

            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd),
            colors = IconButtonDefaults.iconButtonColors(Color.White)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                tint = if (isFavorite) Color.Red else Color.Gray
            )
        }
    }
}

// Detalles del evento
@Composable
fun EventDetails(event: Event) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = event.date.toString(), color = Color.Blue)
        Text(
            text = event.name_event,
            color = Color.Black,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Características clave (Tiempo y Precio)
@Composable
fun EventCharacteristics(event: Event) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        CharacteristicEvent("Tiempo", "${event.duration} Horas")
        CharacteristicEvent("Precio", "${event.price} €")
    }
}

@Composable
fun CharacteristicEvent(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = if (label == "Precio") Icons.Default.AttachMoney else Icons.Default.AvTimer,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "$label: $value",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

// Descripción del evento
@Composable
fun EventDescription(event: Event) {
    event.description?.let {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Acerca del evento",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = it,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun WillGoButtons(
    event: Event,
    goAlone: () -> Unit,
) {
    var willGo by remember { mutableStateOf<WillGo?>(null) }
    var alone by remember { mutableStateOf(false) }  // Estado para saber si el usuario va solo
    var currentAttendants by remember { mutableStateOf(0)}
    val coroutineScope = rememberCoroutineScope()

    // Carga inicial de datos
    LaunchedEffect(event) {
        willGo = getWillGo(event)
        alone = willGo?.alone ?: false
        currentAttendants = getAttendants(event)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Mostrar asistentes
        Column {
            Text(text = "Asistirán ($currentAttendants)")
            Row {
                // Limitar a los primeros 5 asistentes
                val maxIconsToShow = 5
                val attendantsToShow = List(minOf(currentAttendants, maxIconsToShow)) { Icons.Default.AccountCircle }

                // Mostrar los iconos
                attendantsToShow.forEach {
                    Icon(imageVector = it, contentDescription = null)
                }

                // Si hay más de 5 asistentes, mostrar "y más"
                if (currentAttendants > maxIconsToShow) {
                    Text(text = "+${currentAttendants - maxIconsToShow}")
                }
            }
        }

        // Botones de acción
        Row {
            // Botón de WillGo
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (willGo != null) {
                            deleteWillGo(event)
                            willGo = null
                            alone = false
                            currentAttendants--  // Decrementamos los asistentes
                        } else {
                            addWillGo(event)
                            willGo = WillGo(0L, event.id, getUser().nickname, false)
                            currentAttendants++  // Incrementamos los asistentes
                        }
                    }
                }, modifier = Modifier.testTag("attendButton")
            ) {
                Text(text = if (willGo != null) "WillGo ✔" else "WillGo", modifier = Modifier.testTag("willGoText"))
            }

            // Botón de Alone
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (alone) {
                            deleteAlone(event)
                            alone = !alone// Eliminar el estado 'alone'
                        } else {
                            // Pasar 'exists' como parámetro para verificar si ya está registrado
                            addAlone(event, willGo != null) { currentAttendants++ }  // Añadir 'alone' si es necesario
                            goAlone()  // Navegar si va solo
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    if (alone) Color.Green else Color.Gray
                )
            ) {
                Text(text = if (alone) "Voy solo ✔" else "Voy solo")
            }
        }
    }
}

// Ubicación del evento
@Composable
fun EventLocation(event: Event) {
    event.location?.let {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Ubicación",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = it, fontSize = 16.sp)
        }
    }
}

// Contacto
@Composable
fun ContactSection(event: Event) {
    event.email_contact?.let {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Contacto",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = it, fontSize = 16.sp)
        }
    }
}

suspend fun getWillGo(event: Event): WillGo? {
    val client = getClient()
    val userNickname = getUser().nickname
    return client.postgrest["WillGo"].select {
        filter {
            and {
                eq("id_event", event.id)
                eq("user", userNickname)
            }
        }
    }.decodeSingleOrNull()
}

suspend fun getAttendants(event: Event): Int {
    val client = getClient()
    val attendees = client.postgrest["WillGo"].select {
        filter { eq("id_event", event.id) }
    }.decodeList<WillGo>()
    return attendees.size
}

// Otros métodos (`addWillGo`, `deleteWillGo`, etc.) se mantienen similares


suspend fun addWillGo(event: Event) {
    val user = getUser()
    val willGo = WillGo(null,event.id, user.nickname, false)
    val client = getClient()
    try {
        willGo?.let {
            client.postgrest["WillGo"].insert(it)
        }
    } catch (e: Exception) {
        Log.e("addWillGo", "El usuario ya está registrado como asistente")
    }
}


suspend fun deleteWillGo(event: Event) {
    val user = getUser()
    val client = getClient()
    client.postgrest["WillGo"].delete {
        filter {
            and {
                eq("id_event", event.id)
                eq("user", user.nickname)
            }
        }
    }
}


suspend fun addAlone(event: Event, exists: Boolean, addAttendants: () -> Unit) {
    val user = getUser()
    val willGo = user.nickname?.let { WillGo(0L, event.id, it, true) }
    val client = getClient()

    if (exists) {
        // Si ya está registrado como asistente, actualizamos solo el campo 'alone'

        client.postgrest["WillGo"].update(
            { set("alone", true) }
        ) {
            filter {
                and {
                    eq("id_event", event.id)
                    eq("user", user.nickname)
                    eq("alone", false)
                }
            }
        }
    } else {
        try {
            // Si no está registrado, insertamos el nuevo registro
            willGo?.let {
                client.postgrest["WillGo"].insert(willGo)
                addAttendants()  // Incrementar el contador de asistentes
            }
        } catch (e: Exception) {
            Log.e("addAlone", "Error al añadir al usuario como solo: $e")
        }
    }
}

suspend fun deleteAlone(event: Event) {
    val user = getUser()
    val client = getClient()

    // Actualiza el campo 'alone' a false para este usuario y evento
    client.postgrest["WillGo"].update(
        { set("alone", false) }
    ) {
        filter {
            and {
                eq("id_event", event.id)
                eq("user", user.nickname)
                eq("alone", true)
            }
        }
    }
}


suspend fun getUser(): User {
    val client = getClient()
    val supabaseResponse = client.postgrest["Usuario"].select()
    val data = supabaseResponse.decodeList<User>()
    Log.e("supabase", data.toString())
    return data[0]

}

suspend fun getUser(nick: String): User {
    val client = getClient()
    val supabaseResponse = client.postgrest["Usuario"].select(){
        filter {
            eq("nickname", nick)
        }
    }
    val data = supabaseResponse.decodeList<User>()
    Log.e("supabase", data.toString())
    return data[0]

}


suspend fun getCarList(eventId: Int): List<SharedCar> {
    Log.d("UUUUUUUUUUUUUUUUUU", "llego a lista de coches eventId=$eventId")
    val client = getClient()
    val supabaseResponse = client.postgrest["Coche_compartido"].select {
        filter { eq("event_id", eventId) }
    }
    return try { supabaseResponse.decodeList<SharedCar>()
    } catch (e: Exception) {
        Log.e("getCarsForEvent", "Error al obtener coches: $e")
        emptyList()
    }
}



private suspend fun getData(){

    val client = getClient()
    val supabaseResponse = client.postgrest["Usuario"].select()
    val data = supabaseResponse.decodeList<User>()
    Log.e("supabase", data.toString())

}

public fun getClient(): SupabaseClient {
    Log.d("OOOOOOOOOOOOOOOOOO", "llego a getClient")
    return createSupabaseClient(
        supabaseUrl = "https://trpgyhwsghxnaakpoftt.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRycGd5aHdzZ2h4bmFha3BvZnR0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjgwMjgwNDcsImV4cCI6MjA0MzYwNDA0N30.IJthecg-DH9rwOob2XE6ANunb6IskxCbMAacducBVPE"
    ){
        install(Postgrest)
    }
}

suspend fun addToFavorite(event: Event) {
    val client = getClient()
    val user = getUser()
    try {
        client.postgrest["Eventos_favoritos"].insert(FavoriteEvent(event_id = event.id, user_nickname = user.nickname))
    } catch (e: Exception) {
        Log.e("addToFavorite", "Error adding to favorites: $e")
    }
}

suspend fun removeFromFavorite(event: Event) {
    val client = getClient()
    val user = getUser()
    try {
        client.postgrest["Eventos_favoritos"].delete {
            filter {
                eq("event_id", event.id)
                eq("user_nickname", user.nickname)
            }
        }
    } catch (e: Exception) {
        Log.e("removeFromFavorite", "Error removing from favorites: $e")
    }
}

suspend fun checkIfFavorite(event: Event): Boolean {
    val client = getClient()
    val user = getUser()

    return try {
        val response = client.postgrest["Eventos_favoritos"]
            .select {
                filter {
                    eq("event_id", event.id)
                    eq("user_nickname", user.nickname)
                }
            }
            .decodeList<FavoriteEvent>()

        response.isNotEmpty() // Returns true if the event is a favorite
    } catch (e: Exception) {
        Log.e("checkIfFavorite", "Error checking favorite status: ${e.message}")
        false // Default to not a favorite if there's an error
    }
}



