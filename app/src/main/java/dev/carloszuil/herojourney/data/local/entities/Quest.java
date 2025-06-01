package dev.carloszuil.herojourney.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Objects;

@Entity(tableName = "quest")
@TypeConverters(QuestStateConverter.class)
public class Quest {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nombre;

    @NonNull
    private QuestState estado;


    private QuestState estadoAnterior;

    public Quest(@NonNull String nombre,
                 @NonNull QuestState estado,
                 QuestState estadoAnterior) {
        this.nombre = nombre;
        this.estado = estado;
        this.estadoAnterior = estadoAnterior;
    }

    /**
     * Constructor secundario para uso en código (por ejemplo, a la hora de crear una nueva Quest
     * sin estadoAnterior). Lo ignoramos para Room, ya que solo usará el constructor de arriba.
     */
    @Ignore
    public Quest(@NonNull String nombre,
                 @NonNull QuestState estado) {
        this(nombre, estado, null);
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    public QuestState getEstado() {
        return estado;
    }

    public void setEstado(@NonNull QuestState estado) {
        this.estado = estado;
    }

    public QuestState getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(QuestState estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    // equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quest)) return false;
        Quest quest = (Quest) o;
        return id == quest.id &&
                nombre.equals(quest.nombre) &&
                estado == quest.estado &&
                Objects.equals(estadoAnterior, quest.estadoAnterior);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, estado, estadoAnterior);
    }

    @NonNull
    @Override
    public String toString() {
        return "Quest{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                ", estadoAnterior=" + estadoAnterior +
                '}';
    }
}
