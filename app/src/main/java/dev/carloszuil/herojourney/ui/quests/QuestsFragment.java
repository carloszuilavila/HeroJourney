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
import dev.carloszuil.herojourney.data.local.entities.Quest;
import dev.carloszuil.herojourney.data.local.entities.QuestState;
import dev.carloszuil.herojourney.databinding.FragmentQuestsBinding;

public class QuestsFragment extends Fragment {

    private FragmentQuestsBinding binding;

    private final List<Quest> exampleQuests = new ArrayList<Quest>() {{
        add(new Quest(0, "Derrotar al dragón", QuestState.QUEST_BOARD_1));
        add(new Quest(1, "Recolectar hierbas", QuestState.QUEST_BOARD_1));
        add(new Quest(2, "Construir el refugio", QuestState.QUEST_BOARD_2));
        add(new Quest(3, "Aprender hechizo de fuego", QuestState.QUEST_BOARD_3));
        add(new Quest(4, "Rescatar al sabio", QuestState.QUEST_BOARD_4));
    }};

    private final List<Quest> questBoard1 = new ArrayList<>();
    private final List<Quest> questBoard2 = new ArrayList<>();
    private final List<Quest> questBoard3 = new ArrayList<>();
    private final List<Quest> questBoard4 = new ArrayList<>();

    private QuestAdapter adapterBoard1;
    private QuestAdapter adapterBoard2;
    private QuestAdapter adapterBoard3;
    private QuestAdapter adapterBoard4;

    private void inicializarQuestsDeEjemplo() {
        questBoard1.clear();
        questBoard2.clear();
        questBoard3.clear();
        questBoard4.clear();
        for (Quest q : exampleQuests) {
            switch (q.getEstado()) {
                case QUEST_BOARD_1:
                    questBoard1.add(q);
                    break;
                case QUEST_BOARD_2:
                    questBoard2.add(q);
                    break;
                case QUEST_BOARD_3:
                    questBoard3.add(q);
                    break;
                case QUEST_BOARD_4:
                    questBoard4.add(q);
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
        adapterBoard1 = getGenericQuestAdapter(new ArrayList<>(questBoard1));
        adapterBoard2 = getGenericQuestAdapter(new ArrayList<>(questBoard2));
        adapterBoard3 = getGenericQuestAdapter(new ArrayList<>(questBoard3));
        adapterBoard4 = getGenericQuestAdapter(new ArrayList<>(questBoard4));

        // RecyclerViews
        binding.recyclerBoard1.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard1.setAdapter(adapterBoard1);

        binding.recyclerBoard2.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard2.setAdapter(adapterBoard2);

        binding.recyclerBoard3.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard3.setAdapter(adapterBoard3);

        binding.recyclerBoard4.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard4.setAdapter(adapterBoard4);

        // Ajustar ancho de columnas
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int columnWidth = (int) (screenWidth * 0.8f);
        binding.questBoard1 .getLayoutParams().width = columnWidth;
        binding.questBoard2   .getLayoutParams().width = columnWidth;
        binding.questBoard3 .getLayoutParams().width = columnWidth;
        binding.questBoard4.getLayoutParams().width = columnWidth;
        binding.questBoard1 .requestLayout();
        binding.questBoard2   .requestLayout();
        binding.questBoard3 .requestLayout();
        binding.questBoard4.requestLayout();

        return binding.getRoot();
    }

    private QuestAdapter getGenericQuestAdapter(@NonNull List<Quest> list) {
        return new QuestAdapter(
                list,
                (quest, isChecked) -> {
                    if (isChecked && quest.getEstado() != QuestState.QUEST_BOARD_4) {
                        changeQuestState(quest, QuestState.QUEST_BOARD_4);
                    } else if (!isChecked && quest.getEstado() == QuestState.QUEST_BOARD_4) {
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
            case QUEST_BOARD_1:   questBoard1.remove(quest); break;
            case QUEST_BOARD_2: questBoard2.remove(quest); break;
            case QUEST_BOARD_3:   questBoard3.remove(quest); break;
            case QUEST_BOARD_4:  questBoard4.remove(quest); break;
        }
        // Guardar estado anterior si será completada
        if (newState == QuestState.QUEST_BOARD_4) {
            quest.setEstadoAnterior(quest.getEstado());
        }
        // Actualizar
        quest.setEstado(newState);
        // Agregar a la nueva lista
        switch (newState) {
            case QUEST_BOARD_1:   questBoard1.add(quest); break;
            case QUEST_BOARD_2: questBoard2.add(quest); break;
            case QUEST_BOARD_3:   questBoard3.add(quest); break;
            case QUEST_BOARD_4:  questBoard4.add(quest); break;
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
        adapterBoard1.updateList(new ArrayList<>(questBoard1));
        adapterBoard2.updateList(new ArrayList<>(questBoard2));
        adapterBoard3.updateList(new ArrayList<>(questBoard3));
        adapterBoard4.updateList(new ArrayList<>(questBoard4));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
