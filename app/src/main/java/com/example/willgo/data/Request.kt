package com.example.willgo.data

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Long? = null,
    val userRequesting: String,
    val userRequested: Long,
    val state: String,
    val nickRequested: String,
    val id_Event: Long
)
