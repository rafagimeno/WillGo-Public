package com.example.willgo.data
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteEvent (
    val event_id: Long,
    val user_nickname: String
)