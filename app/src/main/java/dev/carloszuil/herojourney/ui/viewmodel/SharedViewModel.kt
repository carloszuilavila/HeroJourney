package dev.carloszuil.herojourney.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // LiveData para las tareas completadas
    private val _tareasCompletadas = MutableLiveData<Int>()
    val tareasCompletadas: LiveData<Int> = _tareasCompletadas

    // Actualiza el número de tareas completadas
    fun actualizarTareasCompletadas(completadas: Int) {
        _tareasCompletadas.value = completadas
    }
}