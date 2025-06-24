package dev.carloszuil.herojourney.ui.hero;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.databinding.FragmentHeroBinding;

public class HeroFragment extends Fragment {

    private FragmentHeroBinding binding;

    private final String[] labels = {
            "Stats", "Rewards",
            "Story", "Settings"
    };

    private final int[] icons = {
            R.drawable.star_shine_20px_g0_w300, R.drawable.person_play_20px_g0_w400,
            R.drawable.contract_20px_g0_w400, R.drawable.settings_20px_g0_w400
    };

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentHeroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupButtonGrid();
    }

    private void setupButtonGrid() {
        // Ahora es un LinearLayout vertical
        LinearLayout container = binding.buttonsContainer;

        // Queremos iterar cada fila (LinearLayout) y dentro cada botón (<include>)
        int rowCount = container.getChildCount();

        int labelIndex = 0;
        for (int row = 1; row < rowCount; row++) {
            // row == 0 es el ImageView, saltamos
            View rowView = container.getChildAt(row);
            if (!(rowView instanceof LinearLayout)) continue;

            LinearLayout fila = (LinearLayout) rowView;
            int colCount = fila.getChildCount();
            for (int col = 0; col < colCount && labelIndex < labels.length; col++) {
                View cell = fila.getChildAt(col);
                ImageView iconView = cell.findViewById(R.id.icon);
                TextView textView = cell.findViewById(R.id.text);

                textView.setText(labels[labelIndex]);
                iconView.setImageResource(icons[labelIndex]);

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.heroFragment, false) // Mantenemos HeroFragment en el backstack
                        .build();

                final int idx = labelIndex;
                cell.setOnClickListener(v -> {
                    switch (idx) {
                        case 0:
                            Toast.makeText(requireContext(), "Cooming soon: " + labels[idx], Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(requireContext(), "Cooming soon: " + labels[idx], Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Intent intentStory = new Intent(requireContext(), StoryActivity.class);
                            startActivity(intentStory);
                            break;
                        case 3:
                            Intent intentSettings = new Intent(requireContext(), SettingsActivity.class);
                            startActivity(intentSettings);
                            break;
                        default:
                            Toast.makeText(requireContext(), "Cooming soon: " + labels[idx], Toast.LENGTH_SHORT).show();
                            break;
                    }
                });

                labelIndex++;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
