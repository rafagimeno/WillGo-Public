package com.example.willgo.data.WillGo

import kotlinx.serialization.Serializable

@Serializable
data class WillGoRequest(
    val id: Long,
    val user: String,
)
