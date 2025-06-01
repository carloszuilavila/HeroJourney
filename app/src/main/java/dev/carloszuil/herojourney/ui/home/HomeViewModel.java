package dev.carloszuil.herojourney.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.data.repository.HabitRepository;

public class HomeViewModel extends AndroidViewModel {
    private final HabitRepository repo;
    private final LiveData<List<Habit>> habits;

    public HomeViewModel(@NonNull Application app) {
        super(app);
        repo = new HabitRepository(app);
        habits = repo.getAllHabits();
    }

    public LiveData<List<Habit>> getHabits() {
        return habits;
    }

    public void addHabit(Habit h) {
        repo.insert(h);
    }

    public void updateHabit(Habit h) {
        repo.update(h);
    }

    public void removeHabit(Habit h) {
        repo.delete(h);
    }
}
