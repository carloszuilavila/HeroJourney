package dev.carloszuil.herojourney.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dev.carloszuil.herojourney.data.local.entities.Habit;

@Dao
public interface HabitDao {
    @Query("SELECT * FROM habit")
    LiveData<List<Habit>> getAllHabits();   // <-- LiveData en lugar de Flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("UPDATE habit SET finished = 0")
    void resetAllHabits();
}