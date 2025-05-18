package dev.carloszuil.herojourney.ui.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import dev.carloszuil.herojourney.databinding.FragmentJourneyBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel

class JourneyFragment : Fragment() {

    private var _binding: FragmentJourneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Inicializar el ImageSwitcher con ImageView como contenido
        binding.imageSwitcher.setFactory {
            ImageView(requireContext()).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }




        // Observar tareas completadas
        sharedViewModel.tareasCompletadas.observe(viewLifecycleOwner, Observer { completadas ->
            if (completadas >= 3) {
                binding.imageSwitcher.setImageResource(R.drawable.journey)
            } else {
                binding.imageSwitcher.setImageResource(R.drawable.rest)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}