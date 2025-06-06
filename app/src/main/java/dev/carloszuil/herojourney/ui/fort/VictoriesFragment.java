package dev.carloszuil.herojourney.ui.fort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.audio.SoundManager;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class VictoriesFragment extends Fragment {

    private Button button;
    private TextInputEditText textArea;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_victories, container, false);

        button = root.findViewById(R.id.victoriesButtonClear);
        textArea = root.findViewById(R.id.victoriesTextArea);

        button.setOnClickListener(v -> {
            textArea.setText("");
            Toast.makeText(getContext(), "The page has suddenly burned!", Toast.LENGTH_SHORT).show();
        });

        return root;
    }
}
