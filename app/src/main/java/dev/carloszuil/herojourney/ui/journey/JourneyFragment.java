package dev.carloszuil.herojourney.ui.journey;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.databinding.FragmentJourneyBinding;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class JourneyFragment extends Fragment {

    private static final int GOAL = 3;
    private static final long DURACION = 60 * 60 * 1000L; // 4 horas

    private FragmentJourneyBinding binding;
    private SharedViewModel sharedViewModel;
    private CountDownTimer countDownTimer;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJourneyBinding.inflate(inflater, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        // ImageSwitcher
        binding.imageSwitcher.setFactory(() -> {
            ImageView iv = new ImageView(requireContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return iv;
        });

        // 1) Restaurar flag + timestamp desde prefs
        sharedViewModel.cargarEstado();

        // 2) Inicializar texto del timer
        binding.textTimer.setText("00:00");

        // 3) Observer de completadas
        sharedViewModel.tareasCompletadas().observe(getViewLifecycleOwner(), completadas -> {
            Log.d("HJDebug", "Observer tareasCompletadas → completadas=" + completadas +
                    ", enViaje=" + sharedViewModel.getEnViaje().getValue());
            onTareasCompletadasChanged(completadas);
        });


        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Si ya estabas en viaje, restaurar temporizador
        if (Boolean.TRUE.equals(sharedViewModel.getEnViaje().getValue())) {
            binding.imageSwitcher.setImageResource(R.drawable.journey);
            startOrRestoreTimer();
        } else {
            binding.imageSwitcher.setImageResource(R.drawable.rest);
        }
    }

    private void onTareasCompletadasChanged(int completadas) {
        Boolean enViaje = sharedViewModel.getEnViaje().getValue();

        if (Boolean.TRUE.equals(enViaje)) {
            // Ya estabas en viaje: solo restauramos o cancelamos si bajan de meta
            if (completadas < GOAL) {
                stopTimerAndReset();
            } else {
                // si sigues >= GOAL, aseguramos imagen y timer
                binding.imageSwitcher.setImageResource(R.drawable.journey);
                startOrRestoreTimer();
            }
        } else {
            // No estabas en viaje: decide iniciar o no
            if (completadas >= GOAL) {
                long now = System.currentTimeMillis();
                sharedViewModel.guardarInicioViaje(now);
                sharedViewModel.guardarEstadoViaje(true);
                binding.imageSwitcher.setImageResource(R.drawable.journey);
                startOrRestoreTimer();
            }
            // si completadas < GOAL y enViaje=false, no hacemos nada
        }
    }

    private void startOrRestoreTimer() {
        long inicio = sharedViewModel.getInicioViaje();
        long elapsed = System.currentTimeMillis() - inicio;
        long restante = DURACION - elapsed;
        Log.d("HJDebug", "startOrRestoreTimer → inicio=" + inicio +
                ", elapsed=" + elapsed + ", restante=" + restante);

        if (restante <= 0) {
            stopTimerAndReset();
            return;
        }
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(restante, 1_000) {
            @Override public void onTick(long ms) {
                long min = (ms / 1_000) / 60;
                long sec = (ms / 1_000) % 60;
                binding.textTimer.setText(String.format("%02d:%02d", min, sec));
            }
            @Override public void onFinish() {
                stopTimerAndReset();
            }
        }.start();
    }

    private void stopTimerAndReset() {
        Log.d("HJDebug", "stopTimerAndReset → cancelando viaje y timer");
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        binding.textTimer.setText("00:00");
        binding.imageSwitcher.setImageResource(R.drawable.rest);
        sharedViewModel.guardarEstadoViaje(false);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}
