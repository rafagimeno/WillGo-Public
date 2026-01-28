package com.example.willgo.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Long,
    val description: String?,
    val name_event: String,
    val email_contact: String? = "",
    val phone: Long? = 0,
    val category: Category?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val date: String? = "",
    val price: Float?,
    val image: String?,
    val duration: Float?,
    val asistance: Long?,
    val type: String? = ""
)
