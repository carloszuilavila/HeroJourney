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

        adapter = new HabitExpandableAdapter(
                habit -> homeViewModel.updateHabit(habit),
                this::onSectionToggled,
                this::showDetail
        );

        binding.recyclerHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHabits.setAdapter(adapter);

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
                etName.setError("The new Habit needs a name.");
                etName.requestFocus();
                return;
            }

            Habit newHabit = new Habit(0, habitName, habitDesc, false);
            homeViewModel.addHabit(newHabit);

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
            homeViewModel.removeHabit(habitDetailed);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

