package dev.carloszuil.herojourney.ui.fort;

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
import dev.carloszuil.herojourney.databinding.FragmentFortBinding;

public class FortFragment extends Fragment {

    private FragmentFortBinding binding;

    private final String[] labels = {
            "Revelations", "Reflect",
            "Destiny", "Victories"
    };

    private final int[] icons = {
            R.drawable.visibility_20px_g0_w400, R.drawable.history_edu_20px_g0_w400,
            R.drawable.strategy_20px_g0_w400, R.drawable.person_play_20px_g0_w400
    };

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentFortBinding.inflate(inflater, container, false);
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
                        .setPopUpTo(R.id.fortFragment, false) // Mantenemos HeroFragment en el backstack
                        .build();

                final int idx = labelIndex;
                cell.setOnClickListener(v -> {
                    switch (idx) {
                        case 0:
                            Intent intentRevelations = new Intent(requireContext(), RevelationsActivity.class);
                            startActivity(intentRevelations);
                            break;
                        case 1:
                            Intent intentReflections = new Intent(requireContext(), ReflectionsActivity.class);
                            startActivity(intentReflections);
                            break;
                        case 2:
                            Intent intentDestiny = new Intent(requireContext(), DestinyActivity.class);
                            startActivity(intentDestiny);
                            break;
                        case 3:
                            Intent intentVictories = new Intent(requireContext(), VictoriesActivity.class);
                            startActivity(intentVictories);
                            break;
                        default:
                            Toast.makeText(requireContext(), "Coming soon: " + labels[idx], Toast.LENGTH_SHORT).show();
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
