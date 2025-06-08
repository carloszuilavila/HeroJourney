package dev.carloszuil.herojourney.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.util.Event;
import dev.carloszuil.herojourney.util.PrefsHelper;

public class SharedViewModel extends AndroidViewModel {
    private static final int GOAL = 3;

    private final HabitDao habitDao;
    private final LiveData<List<Habit>> allHabits;
    private final LiveData<Integer> tareasCompletadas;
    private final MutableLiveData<Boolean> enViaje = new MutableLiveData<>();
    private long inicioViaje;

    public enum SoundEffect{
        CHECK,
        SAVE,
        ERROR
    }

    /** LiveData de “evento único” para pedir a la UI que reproduzca un sonido */
    private final MutableLiveData<Event<SoundEffect>> _soundEvent = new MutableLiveData<>();
    public LiveData<Event<SoundEffect>> getSoundEvent() {
        return _soundEvent;
    }

    /** Método que el VM llamará cuando quiera disparar un sonido */
    public void playSound(SoundEffect effect) {
        _soundEvent.setValue(new Event<>(effect));
    }

    public SharedViewModel(@NonNull Application application) {
        super(application);
        habitDao = AppDatabase.getInstance(application).habitDao();

        // Restaurar estado persistente
        boolean viaje = PrefsHelper.loadIsTraveling(application);
        long inicio = PrefsHelper.loadJourneyStartTime(application);
        inicioViaje = inicio;
        enViaje.setValue(viaje);

        // Obtener todos los hábitos
        allHabits = habitDao.getAllHabits();

        // Mapear hábitos a número de completados
        tareasCompletadas = Transformations.map(allHabits, list -> {
            int c = 0;
            for (Habit h : list) if (h.isFinished()) c++;
            return c;
        });

        // Observación permanente para detectar cambios y reaccionar
        tareasCompletadas.observeForever(this::onTasksChanged);
    }

    private void onTasksChanged(int done) {
        if (done >= GOAL) {
            onGoalReached();
        } else {
            onGoalLost();
        }
    }

    private void onGoalReached() {
        Boolean ya = enViaje.getValue();
        if (!Boolean.TRUE.equals(ya)) {
            long now = System.currentTimeMillis();
            inicioViaje = now;
            PrefsHelper.saveJourneyStartTime(getApplication(), now);
            PrefsHelper.saveIsTraveling(getApplication(), true);
            enViaje.setValue(true);
        }
    }

    private void onGoalLost() {
        Boolean ya = enViaje.getValue();
        if (Boolean.TRUE.equals(ya)) {
            PrefsHelper.saveIsTraveling(getApplication(), false);
            enViaje.setValue(false);
        }
    }

    public LiveData<Integer> getTareasCompletadas() {
        return tareasCompletadas;
    }

    public LiveData<Boolean> getEnViaje() {
        return enViaje;
    }

    public long getInicioViaje() {
        return inicioViaje;
    }

    public int getGoal() {
        return GOAL;
    }

    @VisibleForTesting
    public SharedViewModel(@NonNull Application application, HabitDao dao) {
        super(application);
        this.habitDao = dao;

        // Inicializamos allHabits usando el DAO de pruebas:
        this.allHabits = habitDao.getAllHabits();

        this.tareasCompletadas = Transformations.map(
                this.allHabits,
                list -> {
                    int count = 0;
                    for (Habit h : list) if (h.isFinished()) count++;
                    return count;
                });
    }

}
