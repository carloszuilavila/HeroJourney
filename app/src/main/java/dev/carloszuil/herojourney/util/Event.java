package dev.carloszuil.herojourney.util;

import androidx.annotation.Nullable;

/**
 * Wrapper para LiveData que solo debería responderse una vez (por ejemplo: navegación,
 * mostrar Toasts, reproducir un sonido, etc.). Evita que un mismo valor se “re-dispare”
 * al rotar pantalla o al re-suscribirse al LiveData.
 */
public class Event<T> {
    private final T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Devuelve el contenido si no ha sido ya reclamado; en ese caso marca
     * el evento como “handled” y no lo devolverá otra vez.
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Devuelve el contenido incluso si ya se ha reclamado. Usarlo para logging
     * o cuando quieres inspeccionar/cacharrear sin “consumir” el evento.
     */
    public T peekContent() {
        return content;
    }
}
