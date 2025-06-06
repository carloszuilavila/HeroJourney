package dev.carloszuil.herojourney.ui.fort;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.carloszuil.herojourney.R;

public class RevelationsActivity extends AppCompatActivity {
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


