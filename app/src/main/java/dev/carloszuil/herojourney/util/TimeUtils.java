package dev.carloszuil.herojourney.util;

import androidx.annotation.NonNull;

public class TimeUtils {
    @NonNull
    public static String formatMillisToTimer(long milliseconds){
        if(milliseconds < 0){
            return "00:00";
        }
        long totalSeconds = milliseconds / 1_000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static long calculateRemaining(long startTimestamp, long nowTimestamp, long totalDuration){
        long elapsed = nowTimestamp - startTimestamp;
        long remaining = totalDuration - elapsed;
        return remaining > 0 ? remaining : 0;
    }
}
