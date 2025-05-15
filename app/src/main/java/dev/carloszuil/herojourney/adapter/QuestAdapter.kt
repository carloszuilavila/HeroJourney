package dev.carloszuil.herojourney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.model.Quest
import dev.carloszuil.herojourney.model.QuestState

class QuestAdapter(
    initialList: List<Quest>,  // Cambiar para recibir una lista inmutable
    private val onQuestChecked: (Quest, Boolean) -> Unit,
    private val onMoveClicked: (Quest) -> Unit
) : RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    // Crear una lista mutable interna
    private val quests = mutableListOf<Quest>().apply { addAll(initialList) }

    class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questNombre: TextView = itemView.findViewById(R.id.questText)
        val questCheckBox: CheckBox = itemView.findViewById(R.id.questCheckbox)
        val btnMove: ImageButton = itemView.findViewById(R.id.btnMoveQuest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quest, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = quests[position]
        holder.questNombre.text = quest.nombre

        // Quitar listener antes de actualizar el checkbox
        holder.questCheckBox.setOnCheckedChangeListener(null)

        // Mostrar marcado solo si está COMPLETADA
        holder.questCheckBox.isChecked = quest.estado == QuestState.COMPLETADA

        // Reasignar listener
        holder.questCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onQuestChecked(quest, isChecked)
        }

        holder.btnMove.setOnClickListener {
            onMoveClicked(quest)
        }
    }

    override fun getItemCount(): Int = quests.size

    fun updateList(newList: List<Quest>) {
        val diffResult = DiffUtil.calculateDiff(QuestDiffCallback(quests, newList))
        quests.clear()
        quests.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}
