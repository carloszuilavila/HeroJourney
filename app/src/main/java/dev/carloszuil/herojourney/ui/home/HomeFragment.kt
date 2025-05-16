package dev.carloszuil.herojourney.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.carloszuil.herojourney.adapter.HabitExpandableAdapter
import dev.carloszuil.herojourney.adapter.HabitListItem
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
        val names = PrefsHelper.loadHabits(requireContext())
        if (names.isEmpty()) {
            names.addAll(listOf(
                "Elaborar pociones", "Afilar la espada", "Meditar",
                "Entrenar el cuerpo", "Encontrar un herrero"
            ))
        }
        // Cargar completadas y crear objetos Habit
        val completedSet = PrefsHelper.loadCompletedHabits(requireContext())
        names.forEach { name ->
            habitsList.add(Habit(name).apply { completada = completedSet.contains(name) })
        }

        // Cargar estados de secciones y flag
        val (pendSaved, compSaved) = PrefsHelper.loadSectionsExpanded(requireContext())
        pendientesExpandido = pendSaved; completadasExpandido = compSaved
        goalReached = habitsList.count { it.completada } >= 3

        habitAdapter = HabitExpandableAdapter(
            onHabitToggled = { onHabitCheckToggled() },
            onSectionToggled = { title, expanded -> onSectionToggled(title, expanded) }
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

    private fun showAddHabitDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Nombre de la nueva tarea"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Añadir tarea")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty() && habitsList.none { it.nombre == name }) {
                    habitsList.add(Habit(name))
                    // Persistir la lista completa de nombres
                    PrefsHelper.saveHabits(
                        requireContext(), habitsList.map { it.nombre }
                    )
                    habitAdapter.submitHabits(
                        habitsList, pendientesExpandido, completadasExpandido
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
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