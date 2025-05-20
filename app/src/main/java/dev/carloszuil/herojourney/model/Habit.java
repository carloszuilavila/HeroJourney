package dev.carloszuil.herojourney.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Habit {
    private String nombre;
    private String descripcion;
    private boolean completada;

    // Constructor principal (equivalente a todos los parámetros de Kotlin)
    public Habit(@NonNull String nombre, @NonNull String descripcion, boolean completada) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.completada = completada;
    }

    // Constructor secundario con valores por defecto (descripcion = "", completada = false)
    public Habit(@NonNull String nombre) {
        this(nombre, "", false);
    }

    // Getters and setters
    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(@NonNull String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    // equals() y hashCode() (equivalentes al comportamiento de data class)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Habit)) return false;
        Habit habit = (Habit) o;
        return completada == habit.completada &&
                nombre.equals(habit.nombre) &&
                descripcion.equals(habit.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, descripcion, completada);
    }

    // toString() (similar al generated de Kotlin)
    @NonNull
    @Override
    public String toString() {
        return "Habit{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", completada=" + completada +
                '}';
    }
}
