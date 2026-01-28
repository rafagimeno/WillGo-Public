package com.example.willgo.data

import com.example.willgo.data.User.User
import kotlinx.serialization.Serializable

@Serializable
data class SharedCar(
    val event_id: Int,            // Asociado al evento
    val user_nickname: String,           // ID del usuario que lo creó
    val seats_available: Int,      // Plazas libres
    val departure_time: String,    // Hora de salida
    val departure_address: String,  // Dirección de salida
    var isUserJoined: Boolean = false,
    var usersJoined: List<User> = emptyList()
)

