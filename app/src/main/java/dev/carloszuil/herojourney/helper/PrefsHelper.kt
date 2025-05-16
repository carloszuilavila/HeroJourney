package dev.carloszuil.herojourney.helper

import android.content.Context

object PrefsHelper {
    private const val NAME = "hero_journey_prefs"
    private const val KEY_HABITS = "key_habits_list"
    private const val KEY_COMPLETADAS = "key_habits_completed"
    private const val KEY_EXPAND_PENDIENTES = "key_expand_pendientes"
    private const val KEY_EXPAND_COMPLETADAS = "key_expand_completadas"

    private fun prefs(context: Context) =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    // Guardar lista de nombres de tareas
    fun saveHabits(context: Context, habits: List<String>) {
        prefs(context).edit()
            .putStringSet(KEY_HABITS, habits.toSet())
            .apply()
    }

    fun loadHabits(context: Context): MutableList<String> {
        val set = prefs(context).getStringSet(KEY_HABITS, null)
        return if (set != null) set.toMutableList() else mutableListOf()
    }

    fun saveCompletedHabits(context: Context, completed: Set<String>) {
        prefs(context).edit()
            .putStringSet(KEY_COMPLETADAS, completed)
            .apply()
    }

    fun loadCompletedHabits(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_COMPLETADAS, emptySet()) ?: emptySet()

    fun saveSectionsExpanded(context: Context, pend: Boolean, comp: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_EXPAND_PENDIENTES, pend)
            .putBoolean(KEY_EXPAND_COMPLETADAS, comp)
            .apply()
    }

    fun loadSectionsExpanded(context: Context): Pair<Boolean, Boolean> {
        val p = prefs(context).getBoolean(KEY_EXPAND_PENDIENTES, true)
        val c = prefs(context).getBoolean(KEY_EXPAND_COMPLETADAS, false)
        return p to c
    }
}