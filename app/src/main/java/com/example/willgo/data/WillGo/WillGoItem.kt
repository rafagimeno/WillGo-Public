package com.example.willgo.data.WillGo

import kotlinx.serialization.Serializable

@Serializable
data class WillGoItem(
    val willGo: WillGo,
    val name: String?,
    val followers: Int?,
    var isSelected: Boolean? = false
)
