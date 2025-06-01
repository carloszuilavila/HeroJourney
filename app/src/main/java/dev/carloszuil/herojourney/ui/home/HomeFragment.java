// ui/home/HomeFragment.java
package dev.carloszuil.herojourney.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Objects;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.adapter.HabitExpandableAdapter;
import dev.carloszuil.herojourney.audio.SoundManager;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.databinding.FragmentHomeBinding;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private SharedViewModel sharedViewModel;
    private HabitExpandableAdapter adapter;

    private boolean pendientesExpanded = true;
    private boolean completadasExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        SoundManager.getInstance(requireContext());

        adapter = new HabitExpandableAdapter(
                habit -> {
                    // 1) Actualiza el estado del hábito en la BD
                    homeViewModel.updateHabit(habit);
                    // 2) Reproduce el sonido de "check" en la capa de UI
                    if (habit.isFinished()) {
                        sharedViewModel.playSound(SharedViewModel.SoundEffect.CHECK);
                    }
                },
                this::onSectionToggled,
                this::showDetail
        );

        binding.recyclerHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHabits.setAdapter(adapter);

        sharedViewModel.getSoundEvent().observe(getViewLifecycleOwner(), event -> {
            SharedViewModel.SoundEffect effect = event.getContentIfNotHandled();
            if (effect == null) return;

            // La UI decide qué sonido reproducir
            switch (effect) {
                case CHECK:
                    SoundManager.getInstance(requireContext()).playCheck();
                    break;
                case SAVE:
                    SoundManager.getInstance(requireContext()).playSave();
                    break;
                case ERROR:
                    SoundManager.getInstance(requireContext()).playError();
                    break;
            }
        });

        homeViewModel.getHabits().observe(getViewLifecycleOwner(), list -> {
            adapter.submitHabits(list, pendientesExpanded, completadasExpanded);
        });

        sharedViewModel.getTareasCompletadas().observe(getViewLifecycleOwner(), done -> {
            final int GOAL = 3;
            binding.barraProgreso.setMax(GOAL);
            binding.barraProgreso.setProgress(Math.min(done, GOAL));
            binding.textoProgreso.setText(done + "/" + GOAL);
        });

        binding.buttonAddHabit.setOnClickListener(v -> showAddDialog());

        return binding.getRoot();
    }

    private void onSectionToggled(String title, boolean expanded) {
        if ("HABITS".equals(title)) pendientesExpanded = expanded;
        else if ("DONE".equals(title)) completadasExpanded = expanded;
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_habit, null);

        EditText etName = dialogView.findViewById(R.id.inputHabitName);
        EditText etDesc = dialogView.findViewById(R.id.inputHabitDesc);
        Button cancelButton = dialogView.findViewById(R.id.buttonCancel_habit);
        Button saveButton = dialogView.findViewById(R.id.buttonSave_habit);


        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        saveButton.setOnClickListener(v -> {
            String habitName = etName.getText().toString().trim();
            String habitDesc = etDesc.getText().toString().trim();

            if(habitName.isEmpty()){
                sharedViewModel.playSound(SharedViewModel.SoundEffect.ERROR);
                etName.setError("The new Habit needs a name.");
                etName.requestFocus();
                return;
            }

            Habit newHabit = new Habit(0, habitName, habitDesc, false);
            homeViewModel.addHabit(newHabit);

            sharedViewModel.playSound(SharedViewModel.SoundEffect.SAVE);

            alertDialog.dismiss();
        });

        alertDialog.show();

    }

    private void showDetail(Habit habitDetailed) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_habit_detail, null);

        TextView tvTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView tvDesc  = dialogView.findViewById(R.id.dialogDescription);
        Button closeButton = dialogView.findViewById(R.id.buttonClose_detail);
        Button deleteButton = dialogView.findViewById(R.id.buttonDelete_detail);

        tvTitle.setText(habitDetailed.getName());
        tvDesc.setText(habitDetailed.getDescription());

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog(habitDetailed, alertDialog);
        });

        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(Habit habit, AlertDialog detailDialog) {

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(requireContext());
        confirmBuilder.setTitle("Delete Habit");
        confirmBuilder.setMessage("Are you sure you want to delete: " + habit.getName() + "? This action is permanent.");

        // Botón “Sí, eliminar”
        confirmBuilder.setPositiveButton("Delete", (dialog, which) -> {
            // Aquí ejecutas tu lógica real de borrado
            homeViewModel.removeHabit(habit);
            dialog.dismiss();
            detailDialog.dismiss();
        });

        // Botón “Cancelar”
        confirmBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        confirmBuilder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

