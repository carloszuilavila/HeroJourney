package dev.carloszuil.herojourney;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;
import dev.carloszuil.herojourney.ui.viewmodel.SharedViewModel;

public class SharedViewModelIntegrationTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private HabitDao habitDao;
    private SharedViewModel viewModel;

    @Before
    public void setUp() {
        // 1) Base de datos en memoria
        db = Room.inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        habitDao = db.habitDao();

        // 2) ViewModel con el DAO de pruebas
        Application app = ApplicationProvider.getApplicationContext();
        viewModel = new SharedViewModel(app, habitDao);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void tareasCompletadas_inicialmenteCero() throws InterruptedException {
        // Insertamos un hábito sin terminar
        Habit h1 = new Habit(0, "Dormir", "8 horas", false);
        habitDao.insert(h1);

        // Obtenemos el valor inicial de tareasCompletadas (debe ser 0)
        Integer inicial = LiveDataTestUtil.getOrAwaitValue(
                viewModel.getTareasCompletadas(),
                2, TimeUnit.SECONDS);

        assertEquals(Integer.valueOf(0), inicial);
    }

    @Test
    public void tareasCompletadas_alMarcarUnHabito_actualizaACorrecto() throws InterruptedException {
        // 1) Insertamos un hábito sin terminar
        Habit h1 = new Habit(0, "Correr", "5 km", false);
        habitDao.insert(h1);

        // 2) Recuperamos la lista de hábitos para obtener el objeto con el id generado
        List<Habit> lista = LiveDataTestUtil.getOrAwaitValue(
                habitDao.getAllHabits(),
                2, TimeUnit.SECONDS);

        // Debe haber exactamente 1 hábito en la base de datos
        assertEquals(1, lista.size());
        Habit enDb = lista.get(0);

        // 3) Verificamos que inicialmente tareasCompletadas sea 0
        Integer inicial = LiveDataTestUtil.getOrAwaitValue(
                viewModel.getTareasCompletadas(),
                2, TimeUnit.SECONDS);
        assertEquals(Integer.valueOf(0), inicial);

        // 4) Ahora marcamos el hábito recuperado como terminado y lo actualizamos
        enDb.setFinished(true);
        habitDao.update(enDb);

        // 5) Obtenemos el valor tras la actualización (debe ser 1)
        Integer despues = LiveDataTestUtil.getOrAwaitValue(
                viewModel.getTareasCompletadas(),
                2, TimeUnit.SECONDS);
        assertEquals(Integer.valueOf(1), despues);
    }
}
