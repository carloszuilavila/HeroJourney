package dev.carloszuil.herojourney.model

data class Quest(
    val id: Int,
    val nombre: String,
    var estado: QuestState,
    var estadoAnterior: QuestState? = null // ← Nuevo campo opcional
)