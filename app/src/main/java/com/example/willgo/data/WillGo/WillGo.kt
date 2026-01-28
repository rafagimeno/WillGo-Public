package com.example.willgo.data.WillGo

import kotlinx.serialization.Serializable

@Serializable
data class WillGo (
    val id: Long? = null,
    val id_event: Long,
    val user: String,
    val alone: Boolean
)