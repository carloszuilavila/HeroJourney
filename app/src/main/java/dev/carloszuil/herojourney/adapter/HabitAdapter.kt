package dev.carloszuil.herojourney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.model.Habit

class HabitAdapter(
    private val habits: List<Habit>,
    private val onTareaCambiada: () -> Unit // nuevo callback
) : RecyclerView.Adapter<HabitAdapter.TareaViewHolder>() {

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkboxTarea)
        val texto: TextView = view.findViewById(R.id.textoTarea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = habits[position]
        holder.texto.text = tarea.nombre
        holder.checkBox.isChecked = tarea.completada

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = tarea.completada

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            tarea.completada = isChecked
            onTareaCambiada()
        }
    }

    override fun getItemCount(): Int = habits.size
}
