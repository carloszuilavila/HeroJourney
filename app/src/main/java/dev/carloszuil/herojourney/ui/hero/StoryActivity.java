package dev.carloszuil.herojourney.ui.hero;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.carloszuil.herojourney.R;

public class StoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.story_host, new StoryFragment())
                    .commit();
        }
    }
}


