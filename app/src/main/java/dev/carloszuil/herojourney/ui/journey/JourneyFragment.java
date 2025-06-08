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
import androidx.lifecycle.ViewModelProvider;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.databinding.FragmentJourneyBinding;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;
import dev.carloszuil.herojourney.util.TimeUtils;

public class JourneyFragment extends Fragment {

    private static final long JOURNEY_DURATION = 60 * 60 * 1000L; // 1 hora
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

        binding.imageSwitcher.setFactory(() -> {
            ImageView iv = new ImageView(requireContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return iv;
        });

        binding.textTimer.setText("00:00");

        // Observar estado de viaje
        sharedViewModel.getEnViaje().observe(getViewLifecycleOwner(), enViaje -> {
            if (Boolean.TRUE.equals(enViaje)) {
                binding.imageSwitcher.setImageResource(R.drawable.journey);
                startOrRestoreTimer();
            } else {
                stopTimerAndReset();
            }
        });

        return binding.getRoot();
    }

    private void startOrRestoreTimer() {
        long inicio = sharedViewModel.getInicioViaje();
        long now = System.currentTimeMillis();
        long elapsed = now - inicio;
        long restante = JOURNEY_DURATION - elapsed;

        if (restante <= 0) {
            stopTimerAndReset();
            return;
        }

        // Opcional: mostrar de inmediato el valor “restante” antes de que llegue onTick
        binding.textTimer.setText(TimeUtils.formatMillisToTimer(restante));

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(restante, 1_000) {
            @Override
            public void onTick(long ms) {
                binding.textTimer.setText(TimeUtils.formatMillisToTimer(ms));
            }
            @Override
            public void onFinish() {
                stopTimerAndReset();
            }
        }.start();
    }


    private void stopTimerAndReset() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        binding.textTimer.setText("00:00");
        binding.imageSwitcher.setImageResource(R.drawable.rest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}

