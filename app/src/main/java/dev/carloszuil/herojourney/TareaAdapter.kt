package dev.carloszuil.herojourney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TareaAdapter(
    private val tareas: List<Tarea>,
    private val onTareaCambiada: () -> Unit // nuevo callback
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkboxTarea)
        val texto: TextView = view.findViewById(R.id.textoTarea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.texto.text = tarea.nombre
        holder.checkBox.isChecked = tarea.completada

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = tarea.completada

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            tarea.completada = isChecked
            onTareaCambiada()
        }
    }

    override fun getItemCount(): Int = tareas.size
}
