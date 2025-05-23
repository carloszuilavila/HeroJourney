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
    private val onHabitToggled: () -> Unit,
    private val onSectionToggled: (String, Boolean) -> Unit,
    private val onHabitClicked: (Habit) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HabitListItem>()
    private val sectionExpandedState = mutableMapOf<String, Boolean>()
    private var allHabits = listOf<Habit>()

    fun submitHabits(habits: List<Habit>, pendExpanded: Boolean, compExpanded: Boolean) {
        allHabits = habits
        sectionExpandedState["📌 Pendientes"] = pendExpanded
        sectionExpandedState["✅ Completadas"] = compExpanded
        rebuildItems()
    }

    private fun rebuildItems() {
        val pendientes = allHabits.filter { !it.completada }
        val completadas = allHabits.filter { it.completada }

        items.clear()
        val pendExp = sectionExpandedState.getOrDefault("📌 Pendientes", true)
        items.add(HabitListItem.SectionHeader("📌 Pendientes", pendExp))
        if (pendExp) items += pendientes.map { HabitListItem.HabitItem(it) }

        val compExp = sectionExpandedState.getOrDefault("✅ Completadas", false)
        items.add(HabitListItem.SectionHeader("✅ Completadas", compExp))
        if (compExp) items += completadas.map { HabitListItem.HabitItem(it) }

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when(items[position]) {
        is HabitListItem.SectionHeader -> 0
        is HabitListItem.HabitItem -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section_header, parent, false)
            SectionViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_habit, parent, false)
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

    inner class SectionViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.sectionTitle)
        private val switcher: ImageSwitcher = view.findViewById(R.id.expandSwitcher)

        init {
            switcher.setFactory {
                ImageView(view.context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
            }
        }

        fun bind(header: HabitListItem.SectionHeader) {
            title.text = header.title
            val icon = if (header.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            switcher.setImageResource(icon)
            itemView.setOnClickListener {
                header.isExpanded = !header.isExpanded
                sectionExpandedState[header.title] = header.isExpanded
                rebuildItems()
                onSectionToggled(header.title, header.isExpanded)
            }
        }
    }

    inner class HabitViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val check: CheckBox = view.findViewById(R.id.checkboxTarea)
        private val text: TextView = view.findViewById(R.id.textoTarea)

        fun bind(habit: Habit) {
            text.text = habit.nombre
            check.setOnCheckedChangeListener(null)
            check.isChecked = habit.completada
            check.setOnCheckedChangeListener { _, isChecked ->
                habit.completada = isChecked
                rebuildItems()
                onHabitToggled()
            }
            itemView.setOnClickListener {
                onHabitClicked(habit)
            }
        }
    }
}
