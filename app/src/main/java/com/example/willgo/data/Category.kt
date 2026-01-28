package com.example.willgo.data

import com.example.willgo.R
import kotlinx.serialization.Serializable

@Serializable
enum class Category {
    Actuacion_musical,
    Comedia,
    Cultura,
    Deporte,
    Discoteca,
    Teatro
}

sealed class CategorySectionData(
    val category: Category,
    val title: String,
    val imageId: Int,
){
    object Discotecas: CategorySectionData(
        category = Category.Discoteca,
        title = "Discotecas",
        imageId = R.drawable.discoteca,
    )
    object Comedia: CategorySectionData(
        category = Category.Comedia,
        title = "Comedias",
        imageId = R.drawable.comedia,
    )
    object Cultura: CategorySectionData(
        category = Category.Cultura,
        title = "Cultura",
        imageId = R.drawable.cultura,
    )
    object ActuacionMusical: CategorySectionData(
        category = Category.Actuacion_musical,
        title = "Actuación Musical",
        imageId = R.drawable.musica,
    )
    object Teatro: CategorySectionData(
        category = Category.Teatro,
        title = "Teatro",
        imageId = R.drawable.teatro,
    )
    object Deporte: CategorySectionData(
        category = Category.Deporte,
        title = "Deporte",
        imageId = R.drawable.deporte
    )

    companion object {
        val categories = listOf(
            ActuacionMusical,
            Comedia,
            Cultura,
            Discotecas,
            Teatro,
            Deporte
        )
    }


}