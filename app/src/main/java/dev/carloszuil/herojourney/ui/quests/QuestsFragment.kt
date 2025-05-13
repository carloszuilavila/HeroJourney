package dev.carloszuil.herojourney.ui.quests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.carloszuil.herojourney.databinding.FragmentQuestsBinding
import dev.carloszuil.herojourney.model.Quest
import dev.carloszuil.herojourney.model.QuestState
import dev.carloszuil.herojourney.adapter.QuestAdapter

class QuestsFragment : Fragment() {

    private var _binding: FragmentQuestsBinding? = null
    private val binding get() = _binding!!

    // Lista base de ejemplo
    private val exampleQuests = mutableListOf(
        Quest(0, "Derrotar al dragón", QuestState.PENDIENTE),
        Quest(1, "Recolectar hierbas", QuestState.PENDIENTE),
        Quest(2, "Construir el refugio", QuestState.EN_PROGRESO),
        Quest(3, "Aprender hechizo de fuego", QuestState.CONGELADA),
        Quest(4, "Rescatar al sabio", QuestState.COMPLETADA)
    )

    // Listas para cada columna de estado
    private val pendientes = mutableListOf<Quest>()
    private val enProgreso = mutableListOf<Quest>()
    private val congeladas = mutableListOf<Quest>()
    private val completadas = mutableListOf<Quest>()

    private lateinit var adapterPendientes: QuestAdapter
    private lateinit var adapterEnProgreso: QuestAdapter
    private lateinit var adapterCompletadas: QuestAdapter
    private lateinit var adapterCongeladas: QuestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuestsBinding.inflate(inflater, container, false)

        // Crear adaptadores
        adapterPendientes = QuestAdapter(pendientes) { quest, isChecked ->
            if (isChecked) {
                changeQuestState(quest, QuestState.EN_PROGRESO)
            } else {
                changeQuestState(quest, QuestState.PENDIENTE)
            }
        }
        adapterEnProgreso = QuestAdapter(enProgreso) { quest, isChecked ->
            if (isChecked) {
                changeQuestState(quest, QuestState.COMPLETADA)
            } else {
                changeQuestState(quest, QuestState.EN_PROGRESO)
            }
        }
        adapterCongeladas = QuestAdapter(congeladas) { quest, isChecked ->
            if (isChecked) {
                changeQuestState(quest, QuestState.PENDIENTE)
            } else {
                // No se puede desmarcar una misión congelada
            }
        }
        adapterCompletadas = QuestAdapter(completadas) { quest, isChecked ->
            if (isChecked) {
                changeQuestState(quest, QuestState.COMPLETADA)
            } else {
                changeQuestState(quest, QuestState.EN_PROGRESO)
            }
        }
        // Configuración de RecyclerViews
        binding.recyclerPendientes.layoutManager = LinearLayoutManager(context)
        binding.recyclerPendientes.adapter = adapterPendientes

        binding.recyclerProgreso.layoutManager = LinearLayoutManager(context)
        binding.recyclerProgreso.adapter = adapterEnProgreso

        binding.recyclerCongeladas.layoutManager = LinearLayoutManager(context)
        binding.recyclerCongeladas.adapter = adapterCongeladas

        return binding.root
    }

    // Función para cambiar el estado de una misión
    private fun changeQuestState(quest: Quest, newState: QuestState) {
        // Eliminar la misión de su estado actual
        when (quest.estado) {
            QuestState.PENDIENTE -> pendientes.remove(quest)
            QuestState.EN_PROGRESO -> enProgreso.remove(quest)
            QuestState.COMPLETADA -> completadas.remove(quest)
            QuestState.CONGELADA -> congeladas.remove(quest)
        }

        // Cambiar el estado de la misión
        quest.estado = newState

        // Agregar la misión al nuevo estado
        when (newState) {
            QuestState.PENDIENTE -> pendientes.add(quest)
            QuestState.EN_PROGRESO -> enProgreso.add(quest)
            QuestState.COMPLETADA -> completadas.add(quest)
            QuestState.CONGELADA -> congeladas.add(quest)
        }

        // Actualizar las vistas de los RecyclerView
        updateAdapters()
    }

    // Actualiza las listas de los adaptadores
    private fun updateAdapters() {
        adapterPendientes.updateList(pendientes)
        adapterEnProgreso.updateList(enProgreso)
        adapterCongeladas.updateList(congeladas)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}