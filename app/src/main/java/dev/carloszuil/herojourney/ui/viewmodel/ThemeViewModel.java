package dev.carloszuil.herojourney.ui.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import dev.carloszuil.herojourney.util.PrefsHelper;

public class ThemeViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> isDarkMode = new MutableLiveData<>();

    public ThemeViewModel(@NonNull Application application){
        super(application);
        boolean saved = PrefsHelper.isDarkMode(application);
        isDarkMode.setValue(saved);

        AppCompatDelegate.setDefaultNightMode(saved ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

    }

    public LiveData<Boolean> getIsDarkMode(){
        return isDarkMode;
    }

    public void setDarkMode(boolean dark){
        isDarkMode.setValue(dark);
        PrefsHelper.setDarkMode(getApplication(), dark);

        AppCompatDelegate.setDefaultNightMode(dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
