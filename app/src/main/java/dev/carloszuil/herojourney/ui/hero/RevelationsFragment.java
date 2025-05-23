package dev.carloszuil.herojourney.ui.hero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dev.carloszuil.herojourney.R;

public class RevelationsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla únicamente el layout del fragment
        return inflater.inflate(R.layout.fragment_revelations, container, false);
    }
}
