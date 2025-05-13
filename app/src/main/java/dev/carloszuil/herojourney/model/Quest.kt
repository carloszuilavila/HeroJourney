package dev.carloszuil.herojourney.model

data class Quest(
    val id: Int,                // Identificador único
    val nombre: String,         // Nombre de la misión
    var estado: QuestState      // Estado actual de la misión
)