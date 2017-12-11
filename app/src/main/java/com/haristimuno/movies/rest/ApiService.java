package com.haristimuno.movies.rest;

import com.haristimuno.movies.model.Movie;
import com.haristimuno.movies.model.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hector on 02-12-2017.
 */


public interface ApiService {
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Movie getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
