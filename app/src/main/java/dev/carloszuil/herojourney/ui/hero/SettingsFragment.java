package dev.carloszuil.herojourney.ui.hero;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import dev.carloszuil.herojourney.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Hook al cambio de la key
        SwitchPreferenceCompat darkPref =
                findPreference("pref_dark_mode");
        if (darkPref != null) {
            darkPref.setOnPreferenceChangeListener((pref, newValue) -> {
                boolean dark = (Boolean) newValue;
                AppCompatDelegate.setDefaultNightMode(
                        dark
                                ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                );
                // guardado automático via PreferenceDataStore o SharedPrefs
                return true; // aplicar el cambio
            });
        }
    }
}

