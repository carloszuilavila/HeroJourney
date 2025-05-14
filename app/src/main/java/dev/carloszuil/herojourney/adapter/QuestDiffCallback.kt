package dev.carloszuil.herojourney.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.carloszuil.herojourney.model.Quest

class QuestDiffCallback(
    private val oldList: List<Quest>,
    private val newList: List<Quest>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Mismo ID => misma quest
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Revisa cambios relevantes
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}