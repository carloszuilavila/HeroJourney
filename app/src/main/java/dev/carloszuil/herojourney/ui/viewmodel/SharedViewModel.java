package dev.carloszuil.herojourney.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    // LiveData para las tareas completadas
    private final MutableLiveData<Integer> _tareasCompletadas = new MutableLiveData<>();
    private final LiveData<Integer> tareasCompletadas = _tareasCompletadas;

    // Getter público para observar desde fuera
    public LiveData<Integer> tareasCompletadas() {
        return tareasCompletadas;
    }

    // Actualiza el número de tareas completadas
    public void actualizarTareasCompletadas(int completadas) {
        _tareasCompletadas.setValue(completadas);
    }
}

