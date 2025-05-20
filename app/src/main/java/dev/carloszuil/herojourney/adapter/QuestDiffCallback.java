package dev.carloszuil.herojourney.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import dev.carloszuil.herojourney.model.Quest;

public class QuestDiffCallback extends DiffUtil.Callback {

    private final List<Quest> oldList;
    private final List<Quest> newList;

    public QuestDiffCallback(@NonNull List<Quest> oldList, @NonNull List<Quest> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Mismo ID => misma quest
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Revisa cambios relevantes (usa equals si Quest implementa equals correctamente)
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
