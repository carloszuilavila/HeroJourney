package dev.carloszuil.herojourney.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.adapter.HabitExpandableAdapter;
import dev.carloszuil.herojourney.databinding.FragmentHomeBinding;
import dev.carloszuil.herojourney.helper.PrefsHelper;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HabitExpandableAdapter habitAdapter;
    private SharedViewModel sharedViewModel;

    // Lista de tareas con persistencia
    private final List<Habit> habitsList = new ArrayList<>();

    private boolean pendientesExpandido = true;
    private boolean completadasExpandido = false;
    private boolean goalReached = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        habitsList.clear();

        // Cargar nombres guardados o preset si vacío
        List<Habit> habits = PrefsHelper.loadHabits(requireContext());
        if (habits.isEmpty()) {
            habits.add(new Habit("Elaborar pociones"));
            habits.add(new Habit("Afilar la espada"));
            habits.add(new Habit("Meditar"));
            habits.add(new Habit("Entrenar el cuerpo"));
            habits.add(new Habit("Encontrar un herrero"));
            PrefsHelper.saveHabits(requireContext(), habits);
        }
        // Cargar completadas y asignar al modelo
        Set<String> completedSet = PrefsHelper.loadCompletedHabits(requireContext());
        for (Habit h : habits) {
            h.setCompletada(completedSet.contains(h.getNombre()));
        }
        habitsList.addAll(habits);

        // Cargar estados de secciones y flag
        PrefsHelper.Pair<Boolean, Boolean> sections = PrefsHelper.loadSectionsExpanded(requireContext());
        pendientesExpandido   = sections.first;
        completadasExpandido  = sections.second;
        goalReached = countCompleted() >= 3;

        habitAdapter = new HabitExpandableAdapter(
                () -> onHabitCheckToggled(),
                (title, expanded) -> onSectionToggled(title, expanded),
                habit -> showDetailHabit(habit)
        );
        binding.recyclerHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHabits.setAdapter(habitAdapter);

        binding.buttonAddHabit.setOnClickListener(v -> showAddHabitDialog());

        actualizarListaYProgreso();
        return binding.getRoot();
    }

    private int countCompleted() {
        int count = 0;
        for (Habit h : habitsList) {
            if (h.isCompletada()) count++;
        }
        return count;
    }

    private void removeHabit(Habit habit) {
        List<Habit> updated = new ArrayList<>();
        for (Habit h : habitsList) {
            if (!h.equals(habit)) updated.add(h);
        }
        habitsList.clear();
        habitsList.addAll(updated);
        PrefsHelper.saveHabits(requireContext(), habitsList);
        habitAdapter.submitHabits(habitsList, true, false);
    }

    private void showDetailHabit(Habit habit) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_habit_detail, null);
        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        TextView descView  = dialogView.findViewById(R.id.dialogDescription);

        titleView.setText(habit.getNombre());
        descView.setText(habit.getDescripcion());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View deleteBtn = dialogView.findViewById(R.id.btnDelete);
        View closeBtn  = dialogView.findViewById(R.id.btnClose);
        deleteBtn.setOnClickListener(v -> {
            removeHabit(habit);
            dialog.dismiss();
        });
        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showAddHabitDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_habit, null);
        EditText inputName = dialogView.findViewById(R.id.inputHabitName);
        EditText inputDesc = dialogView.findViewById(R.id.inputHabitDesc);

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir tarea")
                .setView(dialogView)
                .setPositiveButton("Guardar", (DialogInterface dialog, int which) -> {
                    String name = inputName.getText().toString().trim();
                    String desc = inputDesc.getText().toString().trim();
                    if (!name.isEmpty() &&
                            habitsList.stream().noneMatch(h -> h.getNombre().equals(name))) {
                        habitsList.add(new Habit(name, desc, false));
                        PrefsHelper.saveHabits(requireContext(), habitsList);
                        habitAdapter.submitHabits(habitsList,
                                pendientesExpandido, completadasExpandido);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .show();
    }

    private void onHabitCheckToggled() {
        Set<String> completed = new HashSet<>();
        for (Habit h : habitsList) {
            if (h.isCompletada()) completed.add(h.getNombre());
        }
        PrefsHelper.saveCompletedHabits(requireContext(), completed);
        actualizarProgreso();
    }

    private void onSectionToggled(String title, boolean expanded) {
        if ("📌 Pendientes".equals(title)) {
            pendientesExpandido = expanded;
        } else if ("✅ Completadas".equals(title)) {
            completadasExpandido = expanded;
        }
        PrefsHelper.saveSectionsExpanded(
                requireContext(), pendientesExpandido, completadasExpandido);
        habitAdapter.submitHabits(
                habitsList, pendientesExpandido, completadasExpandido);
    }

    private void actualizarProgreso() {
        int completadasCount = countCompleted();
        int requeridas = 3;
        int progresoVisual = Math.min(completadasCount, requeridas);

        binding.barraProgreso.setProgress(progresoVisual);
        binding.textoProgreso.setText(
                completadasCount + "/" + requeridas + " tareas");
        sharedViewModel.actualizarTareasCompletadas(completadasCount);

        if (!goalReached && completadasCount >= requeridas) {
            PrefsHelper.saveJourneyStartTime(
                    requireContext(), System.currentTimeMillis());
            PrefsHelper.saveIsTraveling(requireContext(), true);
            Toast.makeText(requireContext(),
                    "El Viaje del Héroe continúa...", Toast.LENGTH_LONG).show();
            goalReached = true;
        } else if (completadasCount < requeridas) {
            goalReached = false;
            PrefsHelper.saveIsTraveling(requireContext(), false);
        }
    }

    private void actualizarListaYProgreso() {
        habitAdapter.submitHabits(
                habitsList, pendientesExpandido, completadasExpandido);
        actualizarProgreso();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

