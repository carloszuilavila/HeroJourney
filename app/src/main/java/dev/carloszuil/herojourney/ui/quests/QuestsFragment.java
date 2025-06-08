package dev.carloszuil.herojourney.ui.quests;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.adapter.QuestAdapter;
import dev.carloszuil.herojourney.audio.SoundManager;
import dev.carloszuil.herojourney.data.local.entities.Quest;
import dev.carloszuil.herojourney.data.local.entities.QuestState;
import dev.carloszuil.herojourney.databinding.FragmentQuestsBinding;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class QuestsFragment extends Fragment {

    private FragmentQuestsBinding binding;
    private QuestViewModel questViewModel;
    private SharedViewModel sharedViewModel;

    // Adaptadores para cada columna (tableros)
    private QuestAdapter adapterBoard1;
    private QuestAdapter adapterBoard2;
    private QuestAdapter adapterBoard3;
    private QuestAdapter adapterBoard4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        binding = FragmentQuestsBinding.inflate(inflater, container, false);

        // Conseguir el ViewModel
        questViewModel = new ViewModelProvider(this)
                .get(QuestViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        SoundManager.getInstance(requireContext());

        // Crear adaptadores con listas inicialmente vacías
        adapterBoard1 = getGenericQuestAdapter(new ArrayList<>());
        adapterBoard2 = getGenericQuestAdapter(new ArrayList<>());
        adapterBoard3 = getGenericQuestAdapter(new ArrayList<>());
        adapterBoard4 = getGenericQuestAdapter(new ArrayList<>());

        // Asignar LayoutManager y Adapter a los RecyclerViews
        binding.recyclerBoard1.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard1.setAdapter(adapterBoard1);

        binding.recyclerBoard2.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard2.setAdapter(adapterBoard2);

        binding.recyclerBoard3.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard3.setAdapter(adapterBoard3);

        binding.recyclerBoard4.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBoard4.setAdapter(adapterBoard4);

        // Ajustar dimensión de columnas
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int columnWidth = (int) (screenWidth * 0.8f);

        binding.questBoard1.getLayoutParams().width = columnWidth;
        binding.questBoard2.getLayoutParams().width = columnWidth;
        binding.questBoard3.getLayoutParams().width = columnWidth;
        binding.questBoard4.getLayoutParams().width = columnWidth;

        binding.questBoard1.requestLayout();
        binding.questBoard2.requestLayout();
        binding.questBoard3.requestLayout();
        binding.questBoard4.requestLayout();

        // Observamos la lista de Quest desde Room
        questViewModel.getAllQuests().observe(getViewLifecycleOwner(), this::onQuestChanged);

        sharedViewModel.getSoundEvent().observe(getViewLifecycleOwner(), event -> {
            SharedViewModel.SoundEffect effect = event.getContentIfNotHandled();
            if (effect == null) return;

            // La UI decide qué sonido reproducir
            switch (effect) {
                case CHECK:
                    SoundManager.getInstance(requireContext()).playCheck(requireContext());
                    break;
                case SAVE:
                    SoundManager.getInstance(requireContext()).playSave(requireContext());
                    break;
                case ERROR:
                    SoundManager.getInstance(requireContext()).playError(requireContext());
                    break;
            }
        });

        // Lógica para los botones “New Quest” en los tableros 1, 2 y 3
        setupAddButtons();

        return binding.getRoot();
    }

    /**
     * Este método se llama cada vez que cambia la lista entera de quests en la BD.
     * Separa las quests por estado (1/2/3/4) y notifica a cada adaptador.
     */
    private void onQuestChanged(List<Quest> allQuests){
        List<Quest> board1 = new ArrayList<>();
        List<Quest> board2 = new ArrayList<>();
        List<Quest> board3 = new ArrayList<>();
        List<Quest> board4 = new ArrayList<>();

        for(Quest quest : allQuests){
            switch (quest.getEstado()){
                case QUEST_BOARD_1:
                    board1.add(quest);
                    break;
                case QUEST_BOARD_2:
                    board2.add(quest);
                    break;
                case QUEST_BOARD_3:
                    board3.add(quest);
                    break;
                case QUEST_BOARD_4:
                    board4.add(quest);
                    break;
            }
        }

        // Actualiza cada adaptador usando DiffUtil internamente
        adapterBoard1.updateList(board1);
        adapterBoard2.updateList(board2);
        adapterBoard3.updateList(board3);
        adapterBoard4.updateList(board4);
    }

    /**
     * Crea un QuestAdapter que, al hacer check, cambia el estado a COMPLETADA (“BOARD_4”),
     * y al desmarcarlo lo regresa a su estado anterior.
     */
    private QuestAdapter getGenericQuestAdapter(@NonNull List<Quest> initialList){
        return new QuestAdapter(

                initialList,

                (quest, isChecked) -> {
                    if(isChecked && quest.getEstado() != QuestState.QUEST_BOARD_4){
                        // Guardar el estado anterior
                        quest.setEstadoAnterior(quest.getEstado());
                        quest.setEstado(QuestState.QUEST_BOARD_4);
                        questViewModel.updateQuest(quest);
                        // Reproducir efecto de sonido
                        sharedViewModel.playSound(SharedViewModel.SoundEffect.CHECK);
                    } else if (!isChecked && quest.getEstado() == QuestState.QUEST_BOARD_4) {
                        // Restaurar el estado anterior si existe
                        if (quest.getEstadoAnterior() != null) {
                            quest.setEstado(quest.getEstadoAnterior());
                            questViewModel.updateQuest(quest);
                        }
                    }
                },
                quest -> showMoveQuestDialog(quest)
        );
    }

    private void setupAddButtons(){
        binding.buttonAddQuest1.setOnClickListener(v -> {
            showAddQuestDialog(QuestState.QUEST_BOARD_1);
        });

        binding.buttonAddQuest2.setOnClickListener(v -> {
            showAddQuestDialog(QuestState.QUEST_BOARD_2);
        });

        binding.buttonAddQuest3.setOnClickListener(v -> {
            showAddQuestDialog(QuestState.QUEST_BOARD_3);
        });
    }


    private void showAddQuestDialog(QuestState initialState){

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_quest, null);

        EditText editText = dialogView.findViewById(R.id.inputQuestName);
        Button cancelButton = dialogView.findViewById(R.id.buttonCancel_quest);
        Button saveButton = dialogView.findViewById(R.id.buttonSave_quest);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        saveButton.setOnClickListener(v -> {
            String questName = editText.getText().toString().trim();

            if (questName.isEmpty()){
                sharedViewModel.playSound(SharedViewModel.SoundEffect.ERROR);
                editText.setError("The new Quest needs a name.");
                editText.requestFocus();
                return;
            }

            Quest newQuest = new Quest(questName, initialState, null);

            questViewModel.addQuest(newQuest);

            sharedViewModel.playSound(SharedViewModel.SoundEffect.SAVE);

            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    /**
     * Muestra un AlertDialog para mover manualmente la Quest a otro tablero (1→2→3→4).
     * Este método es invocado desde el adapter cuando el usuario pulsa el botón “Mover”.
     */
    private void showMoveQuestDialog(@NonNull Quest quest){
        // Armar la lista de posibles estados (excluyendo el actual)
        List<QuestState> listOtherBoards = new ArrayList<>();
        List<String> labelsList = new ArrayList<>();
        for(QuestState state : QuestState.values()){
            if(state != quest.getEstado()){
                listOtherBoards.add(state);
                String label = state.name().replace("_", " ").toLowerCase();
                label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
                labelsList.add(label);
            }
        }
        String[] options = labelsList.toArray(new String[0]);
        QuestState[] statesArray = listOtherBoards.toArray(new QuestState[0]);

        new AlertDialog.Builder(requireContext())
                .setTitle("Move to: ")
                .setItems(options, (dialog, which) -> {
                    QuestState newState = statesArray[which];
                    // Guardar estado anterior si está completado
                    if (newState == QuestState.QUEST_BOARD_4){
                        quest.setEstadoAnterior(quest.getEstado());
                    }
                    quest.setEstado(newState);
                    questViewModel.updateQuest(quest);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}