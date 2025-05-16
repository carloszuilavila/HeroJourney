package dev.carloszuil.herojourney.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.carloszuil.herojourney.adapter.HabitExpandableAdapter
import dev.carloszuil.herojourney.adapter.HabitListItem
import dev.carloszuil.herojourney.databinding.FragmentHomeBinding
import dev.carloszuil.herojourney.model.Habit
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitAdapter: HabitExpandableAdapter
    private lateinit var sharedViewModel: SharedViewModel

    private val habitsList = mutableListOf(
        Habit("Elaborar pociones"),
        Habit("Afilar la espada"),
        Habit("Meditar"),
        Habit("Entrenar el cuerpo"),
        Habit("Encontrar un herrero")
    )

    private var pendientesExpandido = true
    private var completadasExpandido = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        habitAdapter = HabitExpandableAdapter {
            actualizarListaYProgreso()
        }

        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = habitAdapter

        actualizarListaYProgreso()
        return binding.root
    }

    private fun actualizarProgreso() {
        val completadas = habitsList.count { it.completada }
        val requeridas = 3
        val progresoVisual = minOf(completadas, requeridas)

        binding.barraProgreso.progress = progresoVisual
        binding.textoProgreso.text = "$completadas/$requeridas tareas"

        sharedViewModel.actualizarTareasCompletadas(completadas)

        if (completadas >= requeridas && progresoVisual == requeridas) {
            Toast.makeText(requireContext(), "El Viaje del Héroe continúa...", Toast.LENGTH_LONG).show()
        }
    }


    private fun actualizarListaYProgreso() {
        val completadas = habitsList.filter { it.completada }
        val pendientes = habitsList.filter { !it.completada }

        val items = mutableListOf<HabitListItem>()
        items.add(HabitListItem.SectionHeader("📌 Pendientes", pendientesExpandido))
        if (pendientesExpandido) items += pendientes.map { HabitListItem.HabitItem(it) }

        items.add(HabitListItem.SectionHeader("✅ Completadas", completadasExpandido))
        if (completadasExpandido) items += completadas.map { HabitListItem.HabitItem(it) }

        habitAdapter = HabitExpandableAdapter {
            actualizarProgreso()
        }

        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = habitAdapter

        habitAdapter.submitHabits(habitsList)

        val requeridas = 3
        val progresoVisual = minOf(completadas.size, requeridas)

        binding.barraProgreso.progress = progresoVisual
        binding.textoProgreso.text = "${completadas.size}/$requeridas tareas"

        sharedViewModel.actualizarTareasCompletadas(completadas.size)

        if (completadas.size >= requeridas && progresoVisual == requeridas) {
            Toast.makeText(requireContext(), "El Viaje del Héroe continúa...", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
