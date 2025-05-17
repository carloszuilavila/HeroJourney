package dev.carloszuil.herojourney.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.adapter.HabitExpandableAdapter
import dev.carloszuil.herojourney.databinding.FragmentHomeBinding
import dev.carloszuil.herojourney.helper.PrefsHelper
import dev.carloszuil.herojourney.model.Habit
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitAdapter: HabitExpandableAdapter
    private lateinit var sharedViewModel: SharedViewModel

    // Lista de tareas con persistencia
    private val habitsList = mutableListOf<Habit>()

    private var pendientesExpandido = true
    private var completadasExpandido = false
    private var goalReached = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        habitsList.clear()

        // Cargar nombres guardados o preset si vacío
        val habits = PrefsHelper.loadHabits(requireContext())
        if (habits.isEmpty()) {
            habits.addAll(listOf(
                Habit("Elaborar pociones"),
                Habit("Afilar la espada"),
                Habit("Meditar"),
                Habit("Entrenar el cuerpo"),
                Habit("Encontrar un herrero")
            ))
            PrefsHelper.saveHabits(requireContext(), habits) // Guarda la lista inicial
        }
        // Cargar completadas y crear objetos Habit
        val completedSet = PrefsHelper.loadCompletedHabits(requireContext())
        habits.forEach { habit ->
            habit.completada = completedSet.contains(habit.nombre)
        }
        habitsList.addAll(habits)


        // Cargar estados de secciones y flag
        val (pendSaved, compSaved) = PrefsHelper.loadSectionsExpanded(requireContext())
        pendientesExpandido = pendSaved; completadasExpandido = compSaved
        goalReached = habitsList.count { it.completada } >= 3

        habitAdapter = HabitExpandableAdapter(
            onHabitToggled = { onHabitCheckToggled() },
            onSectionToggled = { title, expanded -> onSectionToggled(title, expanded) },
            onHabitClicked = { habit -> showDetailHabit(habit) }
        )
        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = habitAdapter

        // Botón para añadir nueva tarea
        binding.buttonAddHabit.setOnClickListener {
            showAddHabitDialog()
        }

        actualizarListaYProgreso()
        return binding.root
    }

    private fun removeHabit(habit: Habit) {
        val updatedList = habitsList.filter { it != habit }
        habitsList.clear()
        habitsList.addAll(updatedList)
        PrefsHelper.saveHabits(requireContext(), habitsList)
        habitAdapter.submitHabits(habitsList, pendExpanded = true, compExpanded = false)
    }


    private fun showDetailHabit(habit: Habit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_habit_detail, null)

        val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val descView = dialogView.findViewById<TextView>(R.id.dialogDescription)

        titleView.text = habit.nombre
        descView.text = habit.descripcion

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Elimina el fondo predeterminado (gris) del AlertDialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Configura botones dentro del layout personalizado, si es que los tienes allí
        val deleteButton = dialogView.findViewById<View>(R.id.btnDelete)
        val closeButton = dialogView.findViewById<View>(R.id.btnClose)

        deleteButton?.setOnClickListener {
            removeHabit(habit)
            dialog.dismiss()
        }

        closeButton?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun showAddHabitDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_add_habit, null)
        val inputName = dialogView.findViewById<EditText>(R.id.inputHabitName)
        val inputDesc = dialogView.findViewById<EditText>(R.id.inputHabitDesc)

        AlertDialog.Builder(requireContext())
            .setTitle("Añadir tarea")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = inputName.text.toString().trim()
                val desc = inputDesc.text.toString().trim()
                if (name.isNotEmpty() && habitsList.none { it.nombre == name }) {
                    habitsList.add(Habit(name, desc))
                    PrefsHelper.saveHabits(requireContext(), habitsList)
                    habitAdapter.submitHabits(habitsList, pendientesExpandido, completadasExpandido)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    private fun onHabitCheckToggled() {
        val completedNames = habitsList.filter { it.completada }.map { it.nombre }.toSet()
        PrefsHelper.saveCompletedHabits(requireContext(), completedNames)
        actualizarProgreso()
    }

    private fun onSectionToggled(title: String, expanded: Boolean) {
        when (title) {
            "📌 Pendientes" -> pendientesExpandido = expanded
            "✅ Completadas" -> completadasExpandido = expanded
        }
        PrefsHelper.saveSectionsExpanded(requireContext(), pendientesExpandido, completadasExpandido)
        habitAdapter.submitHabits(habitsList, pendientesExpandido, completadasExpandido)
    }

    private fun actualizarProgreso() {
        val completadasCount = habitsList.count { it.completada }
        val requeridas = 3
        val progresoVisual = minOf(completadasCount, requeridas)

        binding.barraProgreso.progress = progresoVisual
        binding.textoProgreso.text = "$completadasCount/$requeridas tareas"
        sharedViewModel.actualizarTareasCompletadas(completadasCount)

        if (!goalReached && completadasCount >= requeridas) {
            Toast.makeText(requireContext(), "El Viaje del Héroe continúa...", Toast.LENGTH_LONG).show()
            goalReached = true
        }
        if (completadasCount < requeridas) goalReached = false
    }

    private fun actualizarListaYProgreso() {
        habitAdapter.submitHabits(habitsList, pendientesExpandido, completadasExpandido)
        actualizarProgreso()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}