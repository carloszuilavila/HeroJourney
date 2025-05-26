package dev.carloszuil.herojourney.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;

public class HabitRepository {
    private final HabitDao habitDao;
    private final Executor executor;

    // LiveData expuesto para que ViewModel/Fragment puedan observarlo
    private final LiveData<List<Habit>> allHabits;

    public HabitRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        habitDao = db.habitDao();
        allHabits = habitDao.getAllHabits();  // DAO devuelve LiveData<List<Habit>>
        // Un solo hilo para todas las escrituras a la DB
        executor = Executors.newSingleThreadExecutor();
    }

    /** Obtiene todos los hábitos como LiveData */
    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
    }

    /** Inserta un nuevo hábito en background */
    public void insert(final Habit habit) {
        executor.execute(() -> habitDao.insert(habit));
    }

    /** Actualiza un hábito existente en background */
    public void update(final Habit habit) {
        executor.execute(() -> habitDao.update(habit));
    }

    /** Elimina un hábito en background */
    public void delete(final Habit habit) {
        executor.execute(() -> habitDao.delete(habit));
    }
}
