package dev.carloszuil.herojourney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.model.Habit

class HabitExpandableAdapter(
    private val onHabitToggled: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HabitListItem>()
    private val sectionExpandedState = mutableMapOf<String, Boolean>()

    private var allHabits = listOf<Habit>()

    fun submitHabits(habits: List<Habit>) {
        allHabits = habits
        rebuildItems()
    }

    private fun rebuildItems() {
        val pendientes = allHabits.filter { !it.completada }
        val completadas = allHabits.filter { it.completada }

        items.clear()

        val pendientesExpanded = sectionExpandedState.getOrDefault("📌 Pendientes", true)
        items.add(HabitListItem.SectionHeader("📌 Pendientes", pendientesExpanded))
        if (pendientesExpanded) items.addAll(pendientes.map { HabitListItem.HabitItem(it) })

        val completadasExpanded = sectionExpandedState.getOrDefault("✅ Completadas", false)
        items.add(HabitListItem.SectionHeader("✅ Completadas", completadasExpanded))
        if (completadasExpanded) items.addAll(completadas.map { HabitListItem.HabitItem(it) })

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is HabitListItem.SectionHeader -> 0
        is HabitListItem.HabitItem -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section_header, parent, false)
            SectionViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
            HabitViewHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HabitListItem.SectionHeader -> (holder as SectionViewHolder).bind(item)
            is HabitListItem.HabitItem -> (holder as HabitViewHolder).bind(item.habit)
        }
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTitle: TextView = itemView.findViewById(R.id.sectionTitle)
        private val expandSwitcher: ImageSwitcher = itemView.findViewById(R.id.expandSwitcher)

        init {
            expandSwitcher.setFactory {
                ImageView(itemView.context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
            }
        }

        fun bind(header: HabitListItem.SectionHeader) {
            sectionTitle.text = header.title
            val iconRes = if (header.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            expandSwitcher.setImageResource(iconRes)

            itemView.setOnClickListener {
                header.isExpanded = !header.isExpanded
                sectionExpandedState[header.title] = header.isExpanded
                rebuildItems()
            }
        }
    }


    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkboxTarea)
        private val textoTarea: TextView = itemView.findViewById(R.id.textoTarea)  // <--- nuevo

        fun bind(habit: Habit) {
            textoTarea.text = habit.nombre  // <--- asignar texto al TextView
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = habit.completada
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                habit.completada = isChecked
                onHabitToggled()
                rebuildItems()
            }
        }
    }
}
