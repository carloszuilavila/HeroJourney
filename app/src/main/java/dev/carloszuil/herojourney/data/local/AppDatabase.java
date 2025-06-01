package dev.carloszuil.herojourney.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.dao.QuestDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.data.local.entities.Quest;
import dev.carloszuil.herojourney.data.local.entities.QuestStateConverter;

@Database(entities = {Habit.class, Quest.class}, version = 1, exportSchema = false)
@TypeConverters({QuestStateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract HabitDao habitDao();
    public abstract QuestDao questDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "hero_journey.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}