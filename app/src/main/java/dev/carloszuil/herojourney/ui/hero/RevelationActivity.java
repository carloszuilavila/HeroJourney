package dev.carloszuil.herojourney.ui.hero;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.carloszuil.herojourney.R;

public class RevelationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revelations);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.revelation_host, new RevelationsFragment())
                    .commit();
        }
    }
}


