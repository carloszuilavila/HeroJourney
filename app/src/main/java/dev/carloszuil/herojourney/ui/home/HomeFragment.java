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
    private SharedViewModel sharedViewModel;
    private HomeViewModel vm;
    private HabitExpandableAdapter adapter;

    private boolean pendientesExpanded = true;
    private boolean completadasExpanded = false;
    private static final int GOAL = 3;

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(this)
                .get(HomeViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        Log.d("HJDebug", "HomeFragment.onCreateView: antes de cargarEstado, enViaje="
                + sharedViewModel.getEnViaje().getValue());

        adapter = new HabitExpandableAdapter(
                habit -> {                 // aquí recibo el habit que cambió
                    vm.updateHabit(habit);
                },
                (title, expanded) -> onSectionToggled(title, expanded),
                habit -> showDetail(habit)
        );

        binding.recyclerHabits.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.recyclerHabits.setAdapter(adapter);

        // restaurar el estado antes de suscribirse a la lista de habitos
        sharedViewModel.cargarEstado();

        Log.d("HJDebug", "HomeFragment.onCreateView: después de cargarEstado, enViaje="
                + sharedViewModel.getEnViaje().getValue());

        vm.getHabits().observe(getViewLifecycleOwner(), list -> {
            Log.d("HJDebug", "HomeFragment.vm.getHabits: list recibida, enViaje="
                    + sharedViewModel.getEnViaje().getValue());
            renderHabits(list);
        });

        binding.buttonAddHabit.setOnClickListener(v -> showAddDialog());

        return binding.getRoot();
    }

    private void renderHabits(List<Habit> list) {
        adapter.submitHabits(list, pendientesExpanded, completadasExpanded);
        updateProgress(list);
    }

    private void updateProgress(List<Habit> list) {
        long done = list.stream().filter(Habit::isFinished).count();
        Log.d("HJDebug", "updateProgress → done=" + done + ", enViaje=" + sharedViewModel.getEnViaje());

        if (done >= GOAL) {
            sharedViewModel.onGoalReached();
        } else {
            sharedViewModel.onGoalLost();
        }

        // Finalmente actualizamos la barra de progreso
        binding.barraProgreso.setMax(GOAL);
        binding.barraProgreso.setProgress((int)Math.min(done, GOAL));
        binding.textoProgreso.setText(done + "/" + GOAL + " tareas");
    }


    private void onSectionToggled(String title, boolean exp) {
        if ("📌 Pendientes".equals(title)) pendientesExpanded = exp;
        else if ("✅ Completadas".equals(title)) completadasExpanded = exp;
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_habit, null);
        EditText etName = dv.findViewById(R.id.inputHabitName);
        EditText etDesc = dv.findViewById(R.id.inputHabitDesc);

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir tarea")
                .setView(dv)
                .setPositiveButton("Guardar", (DialogInterface d, int w) -> {
                    String n = etName.getText().toString().trim();
                    String des = etDesc.getText().toString().trim();
                    if (!n.isEmpty()) {
                        vm.addHabit(new Habit(0, n, des, false));
                    }
                    d.dismiss();
                })
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .show();
    }

    private void showDetail(Habit h) {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_habit_detail, null);
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
