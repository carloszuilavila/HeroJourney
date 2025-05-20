package dev.carloszuil.herojourney.model

data class Habit(
    val nombre: String,
    var descripcion: String = "",
    var completada: Boolean = false
)
