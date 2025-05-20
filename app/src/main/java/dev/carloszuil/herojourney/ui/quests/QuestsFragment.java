package dev.carloszuil.herojourney.ui.quests;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dev.carloszuil.herojourney.adapter.QuestAdapter;
import dev.carloszuil.herojourney.databinding.FragmentQuestsBinding;
import dev.carloszuil.herojourney.model.Quest;
import dev.carloszuil.herojourney.model.QuestState;

public class QuestsFragment extends Fragment {

    private FragmentQuestsBinding binding;

    private final List<Quest> exampleQuests = new ArrayList<Quest>() {{
        add(new Quest(0, "Derrotar al dragón", QuestState.PENDIENTE));
        add(new Quest(1, "Recolectar hierbas", QuestState.PENDIENTE));
        add(new Quest(2, "Construir el refugio", QuestState.EN_PROGRESO));
        add(new Quest(3, "Aprender hechizo de fuego", QuestState.CONGELADA));
        add(new Quest(4, "Rescatar al sabio", QuestState.COMPLETADA));
    }};

    private final List<Quest> pendientes = new ArrayList<>();
    private final List<Quest> enProgreso = new ArrayList<>();
    private final List<Quest> congeladas = new ArrayList<>();
    private final List<Quest> completadas = new ArrayList<>();

    private QuestAdapter adapterPendientes;
    private QuestAdapter adapterEnProgreso;
    private QuestAdapter adapterCongeladas;
    private QuestAdapter adapterCompletadas;

    private void inicializarQuestsDeEjemplo() {
        pendientes.clear();
        enProgreso.clear();
        congeladas.clear();
        completadas.clear();
        for (Quest q : exampleQuests) {
            switch (q.getEstado()) {
                case PENDIENTE:
                    pendientes.add(q);
                    break;
                case EN_PROGRESO:
                    enProgreso.add(q);
                    break;
                case CONGELADA:
                    congeladas.add(q);
                    break;
                case COMPLETADA:
                    completadas.add(q);
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inicializarQuestsDeEjemplo();
        binding = FragmentQuestsBinding.inflate(inflater, container, false);

        // Adaptadores con listas inmutables
        adapterPendientes  = getGenericQuestAdapter(new ArrayList<>(pendientes));
        adapterEnProgreso  = getGenericQuestAdapter(new ArrayList<>(enProgreso));
        adapterCongeladas  = getGenericQuestAdapter(new ArrayList<>(congeladas));
        adapterCompletadas = getGenericQuestAdapter(new ArrayList<>(completadas));

        // RecyclerViews
        binding.recyclerPendientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPendientes.setAdapter(adapterPendientes);

        binding.recyclerProgreso .setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerProgreso .setAdapter(adapterEnProgreso);

        binding.recyclerCongeladas .setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCongeladas .setAdapter(adapterCongeladas);

        binding.recyclerCompletadas .setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCompletadas .setAdapter(adapterCompletadas);

        // Ajustar ancho de columnas
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int columnWidth = (int) (screenWidth * 0.8f);
        binding.columnaPendientes .getLayoutParams().width = columnWidth;
        binding.columnaProgreso   .getLayoutParams().width = columnWidth;
        binding.columnaCongeladas .getLayoutParams().width = columnWidth;
        binding.columnaCompletadas.getLayoutParams().width = columnWidth;
        binding.columnaPendientes .requestLayout();
        binding.columnaProgreso   .requestLayout();
        binding.columnaCongeladas .requestLayout();
        binding.columnaCompletadas.requestLayout();

        return binding.getRoot();
    }

    private QuestAdapter getGenericQuestAdapter(@NonNull List<Quest> list) {
        return new QuestAdapter(
                list,
                (quest, isChecked) -> {
                    if (isChecked && quest.getEstado() != QuestState.COMPLETADA) {
                        changeQuestState(quest, QuestState.COMPLETADA);
                    } else if (!isChecked && quest.getEstado() == QuestState.COMPLETADA) {
                        if (quest.getEstadoAnterior() != null) {
                            changeQuestState(quest, quest.getEstadoAnterior());
                        }
                    }
                },
                quest -> mostrarDialogoMoverQuest(quest)
        );
    }

    private void changeQuestState(@NonNull Quest quest, @NonNull QuestState newState) {
        // Remover de la lista actual
        switch (quest.getEstado()) {
            case PENDIENTE:   pendientes.remove(quest); break;
            case EN_PROGRESO: enProgreso.remove(quest); break;
            case CONGELADA:   congeladas.remove(quest); break;
            case COMPLETADA:  completadas.remove(quest); break;
        }
        // Guardar estado anterior si será completada
        if (newState == QuestState.COMPLETADA) {
            quest.setEstadoAnterior(quest.getEstado());
        }
        // Actualizar
        quest.setEstado(newState);
        // Agregar a la nueva lista
        switch (newState) {
            case PENDIENTE:   pendientes.add(quest); break;
            case EN_PROGRESO: enProgreso.add(quest); break;
            case CONGELADA:   congeladas.add(quest); break;
            case COMPLETADA:  completadas.add(quest); break;
        }
        updateAdapters();
    }

    private void mostrarDialogoMoverQuest(@NonNull Quest quest) {
        // Opciones de estados distintos al actual
        List<QuestState> disponibles = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (QuestState st : QuestState.values()) {
            if (st != quest.getEstado()) {
                disponibles.add(st);
                String label = st.name().replace("_", " ")
                        .toLowerCase();
                label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
                labels.add(label);
            }
        }
        String[] opciones = labels.toArray(new String[0]);
        QuestState[] estadosArray = disponibles.toArray(new QuestState[0]);

        new AlertDialog.Builder(requireContext())
                .setTitle("Mover a:")
                .setItems(opciones, (DialogInterface dialog, int which) -> {
                    changeQuestState(quest, estadosArray[which]);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateAdapters() {
        adapterPendientes .updateList(new ArrayList<>(pendientes));
        adapterEnProgreso .updateList(new ArrayList<>(enProgreso));
        adapterCongeladas .updateList(new ArrayList<>(congeladas));
        adapterCompletadas.updateList(new ArrayList<>(completadas));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
