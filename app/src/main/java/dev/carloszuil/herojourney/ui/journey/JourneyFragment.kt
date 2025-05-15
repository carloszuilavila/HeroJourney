package dev.carloszuil.herojourney.ui.journey

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.carloszuil.herojourney.databinding.FragmentJourneyBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel

class JourneyFragment : Fragment() {

    private var _binding: FragmentJourneyBinding? = null
    private val binding get() = _binding!!

    // Obtener la instancia del ViewModel compartido
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)

        // Obtener el ViewModel de la actividad
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observar los cambios en las tareas completadas
        sharedViewModel.tareasCompletadas.observe(viewLifecycleOwner, Observer { completadas ->
            if (completadas >= 3) {
                binding.textoEstadoHeroe.text = "El héroe está en el viaje"
                // Imagen en color normal
                binding.imageBackground.clearColorFilter()
                binding.imageBackground.alpha = 1.0f
            } else {
                binding.textoEstadoHeroe.text = "El héroe está descansando"
                // Aplicar filtro gris y opacidad
                binding.imageBackground.setColorFilter(
                    android.graphics.Color.parseColor("#80000000"), // Gris oscuro semitransparente
                    android.graphics.PorterDuff.Mode.SRC_OVER
                )
                binding.imageBackground.alpha = 0.7f
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}