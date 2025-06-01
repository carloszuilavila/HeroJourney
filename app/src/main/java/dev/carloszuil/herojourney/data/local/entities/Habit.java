package dev.carloszuil.herojourney.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "habit")
public class Habit {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "finished")
    private boolean finished;

    /**
     * Constructor principal para Room.
     * Room utilizará este constructor para mapear los campos.
     */
    public Habit(int id, @NonNull String name, @NonNull String description, boolean finished) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.finished = finished;
    }

    /**
     * Constructor secundario con valores por defecto (description="", finished=false).
     * Útil al crear nuevos hábitos, ignorado por Room.
     */
    @Ignore
    public Habit(@NonNull String name) {
        this(0, name, "", false);
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    // equals() y hashCode() para comparación de objetos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Habit)) return false;
        Habit habit = (Habit) o;
        return id == habit.id &&
                finished == habit.finished &&
                name.equals(habit.name) &&
                description.equals(habit.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, finished);
    }

    // toString() para debug
    @NonNull
    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", finished=" + finished +
                '}';
    }
}
