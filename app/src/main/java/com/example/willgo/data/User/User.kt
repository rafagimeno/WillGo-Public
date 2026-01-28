package com.example.willgo.data.User

import com.example.willgo.data.WillGo.WillGoItem
import kotlinx.serialization.Serializable

@Serializable
data class User (
    val nickname: String,
    val name: String,
    val password: String,
    val email: String,
    val followers: Int,
    val followed: Int
) {
    fun toUserResponse(): UserResponse {
        return UserResponse(
            nickname = nickname,
            name = name,
            followers = followers,)
    }



}