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

    private val exampleQuests = mutableListOf(
        Quest(0, "Derrotar al dragón", QuestState.PENDIENTE),
        Quest(1, "Recolectar hierbas", QuestState.PENDIENTE),
        Quest(2, "Construir el refugio", QuestState.EN_PROGRESO),
        Quest(3, "Aprender hechizo de fuego", QuestState.CONGELADA),
        Quest(4, "Rescatar al sabio", QuestState.COMPLETADA)
    )

    private val pendientes = mutableListOf<Quest>()
    private val enProgreso = mutableListOf<Quest>()
    private val congeladas = mutableListOf<Quest>()
    private val completadas = mutableListOf<Quest>()

    private lateinit var adapterPendientes: QuestAdapter
    private lateinit var adapterEnProgreso: QuestAdapter
    private lateinit var adapterCompletadas: QuestAdapter
    private lateinit var adapterCongeladas: QuestAdapter

    private fun inicializarQuestsDeEjemplo() {
        pendientes.clear()
        enProgreso.clear()
        congeladas.clear()
        completadas.clear()

        for (quest in exampleQuests) {
            when (quest.estado) {
                QuestState.PENDIENTE -> pendientes.add(quest)
                QuestState.EN_PROGRESO -> enProgreso.add(quest)
                QuestState.CONGELADA -> congeladas.add(quest)
                QuestState.COMPLETADA -> completadas.add(quest)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        inicializarQuestsDeEjemplo()
        _binding = FragmentQuestsBinding.inflate(inflater, container, false)

        // Inicializar adaptadores con listas inmutables
        adapterPendientes = getGenericQuestAdapter(pendientes.toList())
        adapterEnProgreso = getGenericQuestAdapter(enProgreso.toList())
        adapterCongeladas = getGenericQuestAdapter(congeladas.toList())
        adapterCompletadas = getGenericQuestAdapter(completadas.toList())

        // Asignar RecyclerViews
        binding.recyclerPendientes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPendientes.adapter = adapterPendientes

        binding.recyclerProgreso.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProgreso.adapter = adapterEnProgreso

        binding.recyclerCongeladas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCongeladas.adapter = adapterCongeladas

        binding.recyclerCompletadas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCompletadas.adapter = adapterCompletadas

        // Calcula el ancho disponible
        val screenWidth = resources.displayMetrics.widthPixels
        val factor = 0.8f  // 90% del ancho de la pantalla
        val columnWidth = (screenWidth * factor).toInt()

        binding.columnaPendientes.layoutParams.width = columnWidth
        binding.columnaProgreso.layoutParams.width = columnWidth
        binding.columnaCongeladas.layoutParams.width = columnWidth
        binding.columnaCompletadas.layoutParams.width = columnWidth

        binding.columnaPendientes.requestLayout()
        binding.columnaProgreso.requestLayout()
        binding.columnaCongeladas.requestLayout()
        binding.columnaCompletadas.requestLayout()

        return binding.root
    }


    private fun getGenericQuestAdapter(list: List<Quest>): QuestAdapter {
        return QuestAdapter(list) { quest, isChecked ->
            if (isChecked && quest.estado != QuestState.COMPLETADA) {
                changeQuestState(quest, QuestState.COMPLETADA)
            } else if (!isChecked && quest.estado == QuestState.COMPLETADA) {
                // Comprobamos explícitamente si estadoAnterior no es nulo
                quest.estadoAnterior?.let {
                    changeQuestState(quest, it)
                }
            }
        }
    }

    private fun changeQuestState(quest: Quest, newState: QuestState) {
        // Eliminar de su lista actual
        when (quest.estado) {
            QuestState.PENDIENTE -> pendientes.remove(quest)
            QuestState.EN_PROGRESO -> enProgreso.remove(quest)
            QuestState.CONGELADA -> congeladas.remove(quest)
            QuestState.COMPLETADA -> completadas.remove(quest)
        }

        // Guardar estado anterior si va a completarse
        if (newState == QuestState.COMPLETADA) {
            quest.estadoAnterior = quest.estado
        }

        // Actualizar estado
        quest.estado = newState

        // Agregar a la nueva lista según su estado actual
        when (newState) {
            QuestState.PENDIENTE -> pendientes.add(quest)
            QuestState.EN_PROGRESO -> enProgreso.add(quest)
            QuestState.CONGELADA -> congeladas.add(quest)
            QuestState.COMPLETADA -> completadas.add(quest)
        }

        updateAdapters()
    }

    private fun updateAdapters() {
        // Actualizar las listas en los adaptadores con las nuevas listas inmutables
        adapterPendientes.updateList(pendientes.toList())
        adapterEnProgreso.updateList(enProgreso.toList())
        adapterCongeladas.updateList(congeladas.toList())
        adapterCompletadas.updateList(completadas.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}