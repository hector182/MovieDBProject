package com.haristimuno.movies;

import android.content.Context;

import com.google.gson.Gson;
import com.haristimuno.movies.activity.MainActivity;
import com.haristimuno.movies.cache.CacheStorageImp;
import com.haristimuno.movies.model.Movie;
import com.haristimuno.movies.model.MoviesResponse;
import com.haristimuno.movies.rest.ApiClient;
import com.haristimuno.movies.rest.ApiService;
import org.junit.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;


public class CacheTest {

    private ApiService apiService;
    private final static String API_KEY = "fb4ec6fb7d3a59a0f5c2ffcfa2748832";
    private List<Movie> movies;
    private CacheStorageImp cache;
    private Context ctx;
    private String category="PopularMovies";

    @Test
    public void cache_isCorrect() throws Exception {

        apiService =
                ApiClient.getClient().create(ApiService.class);

        cache = new CacheStorageImp(ctx);

        Call<MoviesResponse> call = apiService.getPopularMovies(MainActivity.API_KEY);

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                movies = response.body().getResults();
                String json = new Gson().toJson(movies);
                cache.save(category, json);
                String localMoviesJson = cache.get(category);
                assertEquals(json, localMoviesJson);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                assertEquals(call, movies.size());
            }
        });
    }
}