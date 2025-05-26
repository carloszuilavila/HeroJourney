package dev.carloszuil.herojourney.data.local.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Quest {
    private int id;
    @NonNull
    private String nombre;
    @NonNull
    private QuestState estado;
    @Nullable
    private QuestState estadoAnterior;

    /** Constructor completo */
    public Quest(int id,
                 @NonNull String nombre,
                 @NonNull QuestState estado,
                 @Nullable QuestState estadoAnterior) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.estadoAnterior = estadoAnterior;
    }

    /** Constructor sin estadoAnterior (por defecto null) */
    public Quest(int id,
                 @NonNull String nombre,
                 @NonNull QuestState estado) {
        this(id, nombre, estado, null);
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

    @Nullable
    public QuestState getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(@Nullable QuestState estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    // equals y hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quest)) return false;
        Quest quest = (Quest) o;
        return id == quest.id
                && nombre.equals(quest.nombre)
                && estado == quest.estado
                && Objects.equals(estadoAnterior, quest.estadoAnterior);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, estado, estadoAnterior);
    }

    // toString

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
