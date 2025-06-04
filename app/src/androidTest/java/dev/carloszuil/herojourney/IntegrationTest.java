package dev.carloszuil.herojourney;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.HabitDao;
import dev.carloszuil.herojourney.data.local.entities.Habit;

public class IntegrationTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private HabitDao habitDao;

    @Before
    public void setUp() {
        // Crea DB en memoria para pruebas
        db = Room.inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        AppDatabase.class)
                .allowMainThreadQueries() // para simplificar en tests
                .build();
        habitDao = db.habitDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertHabit_getAllHabits_returnsInsertedHabit() throws InterruptedException {
        // 1) Creamos el objeto Habit que vamos a insertar
        Habit nuevo = new Habit(0, "Dormir bien", "8 horas mínimo", false);

        // 2) Llamamos al DAO.insert (que devuelve void)
        habitDao.insert(nuevo);

        // 3) Ahora observamos getAllHabits() para recuperar la lista resultante
        CountDownLatch latch = new CountDownLatch(1);
        final List<Habit>[] resultado = new List[1];

        habitDao.getAllHabits().observeForever(list -> {
            resultado[0] = list;
            latch.countDown();
        });

        // 4) Esperamos hasta 2 segundos a que LiveData emita la lista
        if (!latch.await(2, TimeUnit.SECONDS)) {
            fail("LiveData de getAllHabits() no emitió en el tiempo esperado");
        }

        // 5) Verificamos que en la lista haya exactamente 1 elemento
        assertNotNull(resultado[0]);
        assertEquals(1, resultado[0].size());

        // 6) Verificamos que sus campos coincidan con el Habit que insertamos
        Habit enBase = resultado[0].get(0);
        assertEquals("Dormir bien", enBase.getName());
        assertEquals("8 horas mínimo", enBase.getDescription());
        assertFalse(enBase.isFinished());
    }
}
