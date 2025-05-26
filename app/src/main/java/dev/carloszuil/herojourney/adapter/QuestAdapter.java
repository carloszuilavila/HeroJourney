package dev.carloszuil.herojourney.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.data.local.entities.Quest;
import dev.carloszuil.herojourney.data.local.entities.QuestState;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    /** Callback cuando se marca/desmarca una quest */
    public interface OnQuestCheckedListener {
        void onQuestChecked(@NonNull Quest quest, boolean isChecked);
    }

    /** Callback al pulsar mover quest */
    public interface OnMoveClickedListener {
        void onMoveClicked(@NonNull Quest quest);
    }

    private final List<Quest> quests = new ArrayList<>();
    private final OnQuestCheckedListener onQuestChecked;
    private final OnMoveClickedListener onMoveClicked;

    public QuestAdapter(
            @NonNull List<Quest> initialList,
            @NonNull OnQuestCheckedListener onQuestChecked,
            @NonNull OnMoveClickedListener onMoveClicked
    ) {
        this.onQuestChecked = onQuestChecked;
        this.onMoveClicked  = onMoveClicked;
        this.quests.addAll(initialList);
    }

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        public final TextView questNombre;
        public final CheckBox questCheckBox;
        public final ImageButton btnMove;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            questNombre   = itemView.findViewById(R.id.questText);
            questCheckBox = itemView.findViewById(R.id.questCheckbox);
            btnMove       = itemView.findViewById(R.id.btnMoveQuest);
        }
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = quests.get(position);
        holder.questNombre.setText(quest.getNombre());

        // Evitar disparar el listener al reciclar
        holder.questCheckBox.setOnCheckedChangeListener(null);

        // Sólo marcado si está COMPLETADA
        holder.questCheckBox.setChecked(quest.getEstado() == QuestState.QUEST_BOARD_4);

        // Reasignar listener
        holder.questCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onQuestChecked.onQuestChecked(quest, isChecked);
        });

        holder.btnMove.setOnClickListener(v -> onMoveClicked.onMoveClicked(quest));
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    /** Actualiza la lista con DiffUtil para animaciones y cambios eficientes */
    public void updateList(@NonNull List<Quest> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new QuestDiffCallback(this.quests, newList)
        );
        this.quests.clear();
        this.quests.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }
}

