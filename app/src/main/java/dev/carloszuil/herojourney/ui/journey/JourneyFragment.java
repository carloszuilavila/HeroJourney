package dev.carloszuil.herojourney.ui.journey;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.databinding.FragmentJourneyBinding;
import dev.carloszuil.herojourney.helper.PrefsHelper;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class JourneyFragment extends Fragment {

    private FragmentJourneyBinding binding;
    private SharedViewModel sharedViewModel;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJourneyBinding.inflate(inflater, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        // Inicializar ImageSwitcher
        binding.imageSwitcher.setFactory(() -> {
            ImageView iv = new ImageView(requireContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return iv;
        });

        // Cargar y notificar estado inicial de tareas completadas
        int savedCompleted = PrefsHelper.loadCompletedHabits(requireContext()).size();
        sharedViewModel.actualizarTareasCompletadas(savedCompleted);

        // Observar cambios y actualizar imagen
        sharedViewModel.tareasCompletadas().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer completadas) {
                if (completadas >= 3) {
                    binding.imageSwitcher.setImageResource(R.drawable.journey);
                } else {
                    binding.imageSwitcher.setImageResource(R.drawable.rest);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restaurar estado de viaje y arrancar temporizador si corresponde
        restaurarViaje();

        // Observar de nuevo para actualizar UI sin re-grabar prefs
        sharedViewModel.tareasCompletadas().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer completadas) {
                if (completadas >= 3) {
                    binding.imageSwitcher.setImageResource(R.drawable.journey);
                } else {
                    binding.imageSwitcher.setImageResource(R.drawable.rest);
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    binding.textTimer.setText("00:00");
                }
            }
        });
    }

    private void restaurarViaje() {
        boolean enViaje = PrefsHelper.loadIsTraveling(requireContext());
        long inicio   = PrefsHelper.loadJourneyStartTime(requireContext());
        long duracion = 1 * 60 * 1000L;
        long ahora    = System.currentTimeMillis();

        if (enViaje && (ahora - inicio) < duracion) {
            binding.imageSwitcher.setImageResource(R.drawable.journey);
            iniciarTemporizador(inicio, duracion);
        } else {
            binding.imageSwitcher.setImageResource(R.drawable.rest);
            binding.textTimer.setText("00:00");
            PrefsHelper.saveIsTraveling(requireContext(), false);
        }
    }

    private void iniciarTemporizador(long inicio, long duracion) {
        long restante = duracion - (System.currentTimeMillis() - inicio);
        if (restante <= 0) {
            binding.textTimer.setText("00:00");
            PrefsHelper.saveIsTraveling(requireContext(), false);
            return;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(restante, 1000) {
            @Override
            public void onTick(long ms) {
                long min = (ms / 1000) / 60;
                long sec = (ms / 1000) % 60;
                binding.textTimer.setText(
                        String.format("%02d:%02d", min, sec)
                );
            }
            @Override
            public void onFinish() {
                binding.textTimer.setText("00:00");
                binding.imageSwitcher.setImageResource(R.drawable.rest);
                PrefsHelper.saveIsTraveling(requireContext(), false);
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}

