package dev.carloszuil.herojourney.ui.fort;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.carloszuil.herojourney.R;

public class ReflectionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflections);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.reflection_host, new ReflectionsFragment())
                    .commit();
        }
    }
}


