package dev.carloszuil.herojourney.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.carloszuil.herojourney.data.local.entities.Habit;

public class PrefsHelper {

    private static final String NAME = "hero_journey_prefs";
    private static final String KEY_COMPLETADAS = "key_habits_completed";
    private static final String KEY_EXPAND_PENDIENTES = "key_expand_pendientes";
    private static final String KEY_EXPAND_COMPLETADAS = "key_expand_completadas";
    private static final String KEY_IS_TRAVELING = "key_is_traveling";
    private static final String KEY_JOURNEY_START = "key_journey_start";

    private static SharedPreferences prefs(@NonNull Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void saveIsTraveling(@NonNull Context context, boolean isTraveling) {
        prefs(context).edit()
                .putBoolean(KEY_IS_TRAVELING, isTraveling)
                .apply();
    }

    public static boolean loadIsTraveling(@NonNull Context context) {
        return prefs(context).getBoolean(KEY_IS_TRAVELING, false);
    }

    public static void saveJourneyStartTime(@NonNull Context context, long timestamp) {
        prefs(context).edit()
                .putLong(KEY_JOURNEY_START, timestamp)
                .apply();
    }

    public static long loadJourneyStartTime(@NonNull Context context) {
        return prefs(context).getLong(KEY_JOURNEY_START, 0L);
    }

    public static void clearJourneyStartTime(@NonNull Context context) {
        prefs(context).edit()
                .remove(KEY_JOURNEY_START)
                .apply();
    }

    public static void saveHabits(@NonNull Context context, @NonNull List<Habit> habits) {
        Set<String> serialized = new HashSet<>();
        for (Habit habit : habits) {
            serialized.add(habit.getNombre() + "|" + habit.getDescripcion());
        }
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .edit().putStringSet("habits", serialized).apply();
    }

    @NonNull
    public static List<Habit> loadHabits(@NonNull Context context) {
        Set<String> set = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getStringSet("habits", new HashSet<>());

        List<Habit> habits = new ArrayList<>();
        if (set != null) {
            for (String item : set) {
                String[] parts = item.split("\\|", -1);
                String nombre = parts.length > 0 ? parts[0] : "";
                String descripcion = parts.length > 1 ? parts[1] : "";
                habits.add(new Habit(nombre, descripcion, false));
            }
        }
        return habits;
    }

    public static void saveCompletedHabits(@NonNull Context context, @NonNull Set<String> completed) {
        prefs(context).edit()
                .putStringSet(KEY_COMPLETADAS, completed)
                .apply();
    }

    @NonNull
    public static Set<String> loadCompletedHabits(@NonNull Context context) {
        Set<String> set = prefs(context).getStringSet(KEY_COMPLETADAS, new HashSet<>());
        return set != null ? set : new HashSet<>();
    }

    public static void saveSectionsExpanded(@NonNull Context context, boolean pend, boolean comp) {
        prefs(context).edit()
                .putBoolean(KEY_EXPAND_PENDIENTES, pend)
                .putBoolean(KEY_EXPAND_COMPLETADAS, comp)
                .apply();
    }

    @NonNull
    public static Pair<Boolean, Boolean> loadSectionsExpanded(@NonNull Context context) {
        boolean p = prefs(context).getBoolean(KEY_EXPAND_PENDIENTES, true);
        boolean c = prefs(context).getBoolean(KEY_EXPAND_COMPLETADAS, false);
        return new Pair<>(p, c);
    }

    public static class Pair<F, S> {
        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}
