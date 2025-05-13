package dev.carloszuil.herojourney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.model.Quest
import dev.carloszuil.herojourney.model.QuestState

class QuestAdapter(
    private val quests: MutableList<Quest>,
    private val onQuestChecked: (Quest, Boolean) -> Unit
) : RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questNombre: TextView = itemView.findViewById(R.id.questText)
        val questCheckBox: CheckBox = itemView.findViewById(R.id.questCheckbox)
        val cardView: CardView = itemView.findViewById(R.id.questCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quest, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = quests[position]
        holder.questNombre.text = quest.nombre
        holder.questCheckBox.isChecked = quest.estado == QuestState.CONGELADA

        holder.questCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onQuestChecked(quest, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return quests.size
    }

    fun updateList(newList: List<Quest>) {
        quests.clear()
        quests.addAll(newList)
        notifyDataSetChanged()
    }
}
