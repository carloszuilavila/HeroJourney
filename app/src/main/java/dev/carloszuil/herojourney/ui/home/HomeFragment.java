// ui/home/HomeFragment.java
package dev.carloszuil.herojourney.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.adapter.HabitExpandableAdapter;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.databinding.FragmentHomeBinding;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel vm;
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

        vm = new ViewModelProvider(this).get(HomeViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        adapter = new HabitExpandableAdapter(
                habit -> vm.updateHabit(habit),
                this::onSectionToggled,
                this::showDetail
        );

        binding.recyclerHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHabits.setAdapter(adapter);

        vm.getHabits().observe(getViewLifecycleOwner(), list -> {
            adapter.submitHabits(list, pendientesExpanded, completadasExpanded);
        });

        sharedViewModel.getTareasCompletadas().observe(getViewLifecycleOwner(), done -> {
            final int GOAL = 3;
            binding.barraProgreso.setMax(GOAL);
            binding.barraProgreso.setProgress(Math.min(done, GOAL));
            binding.textoProgreso.setText(done + "/" + GOAL + " tareas");
        });

        binding.buttonAddHabit.setOnClickListener(v -> showAddDialog());

        return binding.getRoot();
    }

    private void onSectionToggled(String title, boolean expanded) {
        if ("📌 Pendientes".equals(title)) pendientesExpanded = expanded;
        else if ("✅ Completadas".equals(title)) completadasExpanded = expanded;
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        View dv = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null);
        EditText etName = dv.findViewById(R.id.inputHabitName);
        EditText etDesc = dv.findViewById(R.id.inputHabitDesc);

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir tarea")
                .setView(dv)
                .setPositiveButton("Guardar", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    if (!name.isEmpty()) {
                        vm.addHabit(new Habit(0, name, desc, false));
                    }
                    d.dismiss();
                })
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .show();
    }

    private void showDetail(Habit h) {
        View dv = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_habit_detail, null);
        TextView tvTitle = dv.findViewById(R.id.dialogTitle);
        TextView tvDesc  = dv.findViewById(R.id.dialogDescription);
        tvTitle.setText(h.getName());
        tvDesc.setText(h.getDescription());

        new AlertDialog.Builder(requireContext())
                .setView(dv)
                .setPositiveButton("Eliminar", (d, w) -> {
                    vm.removeHabit(h);
                    d.dismiss();
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

