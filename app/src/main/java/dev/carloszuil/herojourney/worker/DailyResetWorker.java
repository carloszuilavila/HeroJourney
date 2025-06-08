package dev.carloszuil.herojourney.worker;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.util.PrefsHelper;

public class DailyResetWorker extends Worker {

    public DailyResetWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull @Override
    public Result doWork() {
        // 1) Resetea la tabla de hábitos
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        db.habitDao().resetAllHabits();

        // 2) (Opcional) Guarda la fecha de último reset
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PrefsHelper.setLastResetDate(
                    getApplicationContext(),
                    LocalDate.now().toString()
            );
        }

        return Result.success();
    }
}
