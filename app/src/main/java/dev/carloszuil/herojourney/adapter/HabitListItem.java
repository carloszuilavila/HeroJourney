package dev.carloszuil.herojourney.adapter;


import dev.carloszuil.herojourney.model.Habit;

public abstract class HabitListItem {

    // Clase para representar una cabecera de sección
    public static class SectionHeader extends HabitListItem {
        private final String title;
        private boolean isExpanded;

        public SectionHeader(String title, boolean isExpanded) {
            this.title = title;
            this.isExpanded = isExpanded;
        }

        public String getTitle() {
            return title;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }
    }

    // Clase para representar un ítem de hábito
    public static class HabitItem extends HabitListItem {
        private final Habit habit;

        public HabitItem(Habit habit) {
            this.habit = habit;
        }

        public Habit getHabit() {
            return habit;
        }
    }
}
