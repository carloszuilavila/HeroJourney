package dev.carloszuil.herojourney.adapter

import dev.carloszuil.herojourney.model.Habit

sealed class HabitListItem {
    data class SectionHeader(val title: String, var isExpanded: Boolean) : HabitListItem()
    data class HabitItem(val habit: Habit) : HabitListItem()
}
