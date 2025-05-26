package dev.carloszuil.herojourney.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.util.PrefsHelper;

public class SharedViewModel extends AndroidViewModel {
    private static final int GOAL = 3;

    private final HabitDao habitDao;
    private final LiveData<List<Habit>> allHabits;
    private final LiveData<Integer> tareasCompletadas;
    private final MutableLiveData<Boolean> enViaje = new MutableLiveData<>();
    private long inicioViaje;

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
}
