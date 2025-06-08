package dev.carloszuil.herojourney;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataTestUtil {
    public static <T> T getOrAwaitValue(LiveData<T> liveData,
                                        long timeout,
                                        TimeUnit unit) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T value) {
                // Una vez reciba cualquier valor (asumimos que no es null),
                // almacenamos y desmontamos el observer
                data[0] = value;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        // Esperamos hasta que LiveData llame a onChanged, o hasta que expire el timeout
        if (!latch.await(timeout, unit)) {
            liveData.removeObserver(observer);
            throw new AssertionError("LiveData no emitió ningún valor en el tiempo esperado.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}
