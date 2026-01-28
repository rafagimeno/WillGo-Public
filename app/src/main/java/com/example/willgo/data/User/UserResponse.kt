package com.example.willgo.data.User

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val nickname: String,
    val name: String,
    val followers: Int,
)
