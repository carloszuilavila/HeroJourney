package dev.carloszuil.herojourney.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.util.PrefsHelper;

/**
 * SoundManager centraliza la reproducción de efectos (CHECK, SAVE, ERROR)
 * usando MediaPlayer. Cada efecto es un MediaPlayer diferente creado con
 * MediaPlayer.create(...), que ya queda en estado "prepared". Cuando termine
 * la reproducción, el OnCompletionListener lo reposicionará a 0.
 */
public class SoundManager {
    private static SoundManager instance;

    /** Tres MediaPlayer independientes para cada efecto */
    private final MediaPlayer mpCheck;
    private final MediaPlayer mpSave;
    private final MediaPlayer mpError;

    private SoundManager(Context ctx) {
        // Creamos cada MediaPlayer a partir de los raw resources
        mpCheck = MediaPlayer.create(ctx, R.raw.soundeffect_check);
        mpSave  = MediaPlayer.create(ctx, R.raw.soundeffect_save);
        mpError = MediaPlayer.create(ctx, R.raw.soundeffect_error);

        Log.d("HJDebug", "SoundManager ctor → MediaPlayers creados.");

        // Configuramos para que, al terminar, simplemente reposicione a 0
        MediaPlayer.OnCompletionListener resetListener = player -> {
            // Al finalizar la reproducción, lo devolvemos al inicio para la próxima vez
            player.seekTo(0);
        };
        mpCheck.setOnCompletionListener(resetListener);
        mpSave.setOnCompletionListener(resetListener);
        mpError.setOnCompletionListener(resetListener);
    }

    /**
     * Devuelve la instancia singleton de SoundManager (usa ApplicationContext).
     * Debemos llamar a getInstance(...) lo antes posible (idealmente en Activity.onCreate).
     */
    public static synchronized SoundManager getInstance(Context ctx) {
        if (instance == null) {
            // Usamos applicationContext para no filtrar la Activity en el singleton
            instance = new SoundManager(ctx.getApplicationContext());
        }
        return instance;
    }

    /** Reproduce el efecto CHECK */
    public void playCheck(Context context) {
        if (mpCheck == null || !PrefsHelper.isSoundEffectsEnabled(context)) return;
        try {
            if (mpCheck.isPlaying()) {
                // Si está en medio de una reproducción anterior, lo pausamos y lo reposicionamos
                mpCheck.pause();
                mpCheck.seekTo(0);
            }
            Log.d("HJDebug", "SoundManager.playCheck() → start()");
            mpCheck.start();
        } catch (IllegalStateException e) {
            Log.e("HJDebug", "playCheck → IllegalStateException", e);
        }
    }

    /** Reproduce el efecto SAVE */
    public void playSave(Context context) {
        if (mpSave == null || !PrefsHelper.isSoundEffectsEnabled(context)) return;
        try {
            if (mpSave.isPlaying()) {
                mpSave.pause();
                mpSave.seekTo(0);
            }
            Log.d("HJDebug", "SoundManager.playSave() → start()");
            mpSave.start();
        } catch (IllegalStateException e) {
            Log.e("HJDebug", "playSave → IllegalStateException", e);
        }
    }

    /** Reproduce el efecto ERROR */
    public void playError(Context context) {
        if (mpError == null || !PrefsHelper.isSoundEffectsEnabled(context)) return;
        try {
            if (mpError.isPlaying()) {
                mpError.pause();
                mpError.seekTo(0);
            }
            Log.d("HJDebug", "SoundManager.playError() → start()");
            mpError.start();
        } catch (IllegalStateException e) {
            Log.e("HJDebug", "playError → IllegalStateException", e);
        }
    }

    /**
     * Libera todos los MediaPlayers. Llamar, por ejemplo, desde MainActivity.onDestroy().
     */
    public void release() {
        Log.d("HJDebug", "SoundManager.release() → liberando MediaPlayers");
        if (mpCheck != null) {
            mpCheck.release();
        }
        if (mpSave != null) {
            mpSave.release();
        }
        if (mpError != null) {
            mpError.release();
        }
        instance = null;
    }
}