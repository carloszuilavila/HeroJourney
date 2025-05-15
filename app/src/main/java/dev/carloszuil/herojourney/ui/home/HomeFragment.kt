package dev.carloszuil.herojourney.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.carloszuil.herojourney.databinding.FragmentHomeBinding
import dev.carloszuil.herojourney.model.Habit
import dev.carloszuil.herojourney.adapter.HabitAdapter
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitAdapter: HabitAdapter
    private val habitsList = mutableListOf(
        Habit("Planchar"),
        Habit("Lavar los platos"),
        Habit("Sacar la basura"),
        Habit("Hacer ejercicio"),
        Habit("Leer 10 páginas")
    )

    // Obtener la instancia del ViewModel compartido
    private lateinit var sharedViewModel: SharedViewModel

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("HeroJourneyPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Obtener el ViewModel de la actividad
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Adaptador con callback
        habitAdapter = HabitAdapter(habitsList) {
            actualizarProgreso()
        }

        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = habitAdapter

        // Inicializar barra
        binding.barraProgreso.max = 3
        actualizarProgreso()

        return binding.root
    }

    private fun actualizarProgreso() {
        val completadas = habitsList.count { it.completada }
        val requeridas = 3

        // Limita el progreso visual, pero muestra el total real
        val progresoVisual = minOf(completadas, requeridas)

        binding.barraProgreso.progress = progresoVisual
        binding.textoProgreso.text = "$completadas/$requeridas tareas"

        // Actualizar el ViewModel con las tareas completadas
        sharedViewModel.actualizarTareasCompletadas(completadas)

        // Verificar si se alcanzó el límite de tareas
        if (completadas >= requeridas && progresoVisual == requeridas) {
            if (!haMostradoMensaje()) {
                mostrarDialogo()
                marcarMensajeComoMostrado()
            }
        } else {
            // Si no se alcanzó el objetivo y el mensaje ya fue mostrado, se puede restablecer
            if (completadas < requeridas) {
                restablecerMensajeMostrado()
            }
        }
    }

    private fun haMostradoMensaje(): Boolean {
        return sharedPreferences.getBoolean("mensajeMostrado", false)
    }

    private fun marcarMensajeComoMostrado() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("mensajeMostrado", true)
        editor.apply()
    }

    private fun restablecerMensajeMostrado() {
        // Si el progreso baja de la cantidad de tareas requeridas, se puede restablecer el valor
        val editor = sharedPreferences.edit()
        editor.putBoolean("mensajeMostrado", false)
        editor.apply()
    }

    private fun mostrarDialogo() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¡Felicidades!")
        builder.setMessage("Has completado las tareas necesarias para que el héroe inicie su viaje.")
        /*
        builder.setPositiveButton("¡Comenzar viaje!") { dialog, _ ->
            // Aquí puedes iniciar el viaje, por ejemplo:
            iniciarViajeDelHeroe()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }
        */
        builder.show()
    }

    private fun iniciarViajeDelHeroe() {
        // Lógica para iniciar el viaje del héroe
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Viaje del Héroe")
        builder.setMessage("¡Tu aventura continúa!")
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}