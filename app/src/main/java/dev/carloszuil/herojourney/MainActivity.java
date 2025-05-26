package dev.carloszuil.herojourney;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import dev.carloszuil.herojourney.databinding.ActivityMainBinding;
import dev.carloszuil.herojourney.system.ResetScheduler;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Inflar el layout con ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener la BottomNavigationView desde el binding
        BottomNavigationView navView = binding.navView;

        // Configurar NavController
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_activity_main
        );
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);

        // Cada ID de menú es un destino de nivel superior
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.journeyFragment,
                R.id.questsFragment,
                R.id.heroFragment,
                R.id.fortFragment
        ).build();

        // Conectar BottomNavigationView con NavController
        NavigationUI.setupWithNavController(navView, navController);

        ResetScheduler.programarResetDiario(this);
    }
}
