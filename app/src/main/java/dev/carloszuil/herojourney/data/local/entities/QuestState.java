package dev.carloszuil.herojourney.data.local.entities;

public enum QuestState {
    QUEST_BOARD_1("Urgent"),
    QUEST_BOARD_2("Accepted"),
    QUEST_BOARD_3("Available"),
    QUEST_BOARD_4("Completed");

    private final String displayName;

    QuestState(String displayName) {
        this.displayName = displayName;
    }

    /** Nombre legible para el usuario */
    public String getDisplayName() {
        return displayName;
    }
}
