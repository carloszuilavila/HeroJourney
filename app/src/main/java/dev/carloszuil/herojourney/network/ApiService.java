package dev.carloszuil.herojourney.network;

import java.util.List;

import dev.carloszuil.herojourney.model.Revelation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("revelations")
    Call<List<Revelation>> getAllRevelations();

    @GET("revelations/random")
    Call<Revelation> getRevelation(@Query("categorie") String categorie);
}
