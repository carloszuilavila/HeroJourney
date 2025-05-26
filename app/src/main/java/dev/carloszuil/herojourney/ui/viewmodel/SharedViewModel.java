package dev.carloszuil.herojourney.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.util.PrefsHelper;

public class SharedViewModel extends AndroidViewModel {

    private final HabitDao habitDao;
    private final MutableLiveData<Integer> tareasCompletadas = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> enViaje = new MutableLiveData<>(false);
    private long inicioViaje;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        habitDao = AppDatabase.getInstance(application).habitDao();

        // Observar cambios en la lista de hábitos y actualizar solo los completados
        habitDao.getAllHabits().observeForever(habits -> {
            if (habits != null) {
                int count = 0;
                for (Habit h : habits) {
                    if (h.isFinished()) count++;
                }
                tareasCompletadas.setValue(count);
            }
        });
    }

    /**
     * LiveData del número de tareas completadas.
     */
    public LiveData<Integer> tareasCompletadas() {
        return tareasCompletadas;
    }

    /**
     * LiveData que indica si el usuario está en viaje.
     */
    public LiveData<Boolean> getEnViaje() {
        return enViaje;
    }

    /**
     * Timestamp de inicio del viaje.
     */
    public long getInicioViaje() {
        return inicioViaje;
    }

    /**
     * Carga el estado de viaje desde SharedPreferences.
     */
    public void cargarEstado() {
        // 1) Leer primero de Prefs
        boolean viaje = PrefsHelper.loadIsTraveling(getApplication());
        long inicio = PrefsHelper.loadJourneyStartTime(getApplication());

        Log.d("HJDebug", "cargarEstado → viaje=" + viaje + ", inicio=" + inicio);

        // 2) Actualizar campos internos y LiveData
        inicioViaje = inicio;
        enViaje.setValue(viaje);
    }


    /**
     * Guarda el estado de viaje en SharedPreferences y notifica LiveData.
     */
    public void guardarEstadoViaje(boolean viaje) {
        PrefsHelper.saveIsTraveling(getApplication(), viaje);
        enViaje.setValue(viaje);
    }

    /**
     * Guarda el timestamp de inicio de viaje en SharedPreferences.
     */
    public void guardarInicioViaje(long timestamp) {
        inicioViaje = timestamp;
        PrefsHelper.saveJourneyStartTime(getApplication(), timestamp);
    }

    public void onGoalReached() {
        Boolean yaEnViaje = enViaje.getValue();
        Log.d("HJDebug", "onGoalReached → yaEnViaje=" + yaEnViaje);
        if (!Boolean.TRUE.equals(yaEnViaje)) {
            long now = System.currentTimeMillis();
            guardarInicioViaje(now);
            guardarEstadoViaje(true);
            Log.d("HJDebug", "onGoalReached → yaEnViaje=" + yaEnViaje);
        }
    }

    /** Llamar cuando se baje de la meta después de haberla alcanzado */
    public void onGoalLost() {
        Boolean yaEnViaje = enViaje.getValue();
        Log.d("HJDebug", "onGoalLost → yaEnViaje=" + yaEnViaje);
        if (Boolean.TRUE.equals(yaEnViaje)) {
            guardarEstadoViaje(false);
            Log.d("HJDebug", "SharedViewModel → viaje cancelado");
        }
    }
}
