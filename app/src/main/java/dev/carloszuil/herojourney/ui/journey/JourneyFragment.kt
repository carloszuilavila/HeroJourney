package dev.carloszuil.herojourney.ui.journey

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import dev.carloszuil.herojourney.databinding.FragmentJourneyBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import dev.carloszuil.herojourney.R
import dev.carloszuil.herojourney.helper.PrefsHelper
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel

class JourneyFragment : Fragment() {

    private var _binding: FragmentJourneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val savedCompleted = PrefsHelper.loadCompletedHabits(requireContext()).size
        sharedViewModel.actualizarTareasCompletadas(savedCompleted)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3) Leer el estado de viaje y arrancar timer *sólo aquí*
        restaurarViaje()

        // 4) Observar LiveData sólo para *mostrar* la imagen en caliente,
        //    sin volver a guardar inicio de viaje:
        sharedViewModel.tareasCompletadas.observe(viewLifecycleOwner) { completadas ->
            if (completadas >= 3) {
                // si ya estabas viajando, la imagen ya está puesta por restaurarViaje()
                // si marcaste/desmarcaste mientras estás aquí, la actualizas:
                binding.imageSwitcher.setImageResource(R.drawable.journey)
            } else {
                binding.imageSwitcher.setImageResource(R.drawable.rest)
                countDownTimer?.cancel()
                binding.textTimer.text = "00:00"
            }
        }
    }

    private fun restaurarViaje() {
        val ctx = requireContext()
        val enViaje = PrefsHelper.loadIsTraveling(ctx)
        val inicio   = PrefsHelper.loadJourneyStartTime(ctx)
        val duracion = 1 * 60 * 1000L
        val ahora    = System.currentTimeMillis()

        if (enViaje && ahora - inicio < duracion) {
            binding.imageSwitcher.setImageResource(R.drawable.journey)
            iniciarTemporizador(inicio, duracion)
        } else {
            // El viaje caducó o nunca empezó
            binding.imageSwitcher.setImageResource(R.drawable.rest)
            binding.textTimer.text = "00:00"
            PrefsHelper.saveIsTraveling(ctx, false)
        }
    }

    // ⏳ Cuenta atrás que va actualizando texto de timer
    private fun iniciarTemporizador(inicio: Long, duracion: Long) {
        val restante = duracion - (System.currentTimeMillis() - inicio)
        if (restante <= 0) {
            binding.textTimer.text = "00:00"
            PrefsHelper.saveIsTraveling(requireContext(), false)
            return
        }
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(restante, 1_000) {
            override fun onTick(ms: Long) {
                val min = (ms / 1_000) / 60
                val sec = (ms / 1_000) % 60
                binding.textTimer.text = String.format("%02d:%02d", min, sec)
            }
            override fun onFinish() {
                binding.textTimer.text = "00:00"
                binding.imageSwitcher.setImageResource(R.drawable.rest)
                PrefsHelper.saveIsTraveling(requireContext(), false)
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}