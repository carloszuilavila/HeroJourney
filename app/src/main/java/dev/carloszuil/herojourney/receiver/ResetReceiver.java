package dev.carloszuil.herojourney.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.Executors;

import dev.carloszuil.herojourney.data.local.AppDatabase;

public class ResetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppDatabase db = AppDatabase.getInstance(context);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.habitDao().resetAllHabits();
        });
    }
}
