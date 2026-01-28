package com.example.willgo.data

import kotlinx.serialization.Serializable

@Serializable
data class Comment (
    val id:Long,
    val created_at:String?,
    val user_nickname:String,
    val event_id:Long,
    val comment:String,
) {

}