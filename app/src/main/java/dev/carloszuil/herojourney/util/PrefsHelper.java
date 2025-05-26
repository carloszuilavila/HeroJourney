package dev.carloszuil.herojourney.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {

    private static final String PREF_NAME = "hero_journey_prefs";
    private static final String KEY_IS_TRAVELING = "is_traveling";
    private static final String KEY_JOURNEY_START = "journey_start";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean loadIsTraveling(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_TRAVELING, false);
    }

    public static void saveIsTraveling(Context context, boolean isTraveling) {
        getPrefs(context).edit().putBoolean(KEY_IS_TRAVELING, isTraveling).apply();
    }

    public static long loadJourneyStartTime(Context context) {
        return getPrefs(context).getLong(KEY_JOURNEY_START, 0L);
    }

    public static void saveJourneyStartTime(Context context, long timestamp) {
        getPrefs(context).edit().putLong(KEY_JOURNEY_START, timestamp).apply();
    }
}
