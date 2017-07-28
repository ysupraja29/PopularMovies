package com.supraja_y.popularmovies.RetroFit;

import com.supraja_y.popularmovies.BuildConfig;
import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.POJOS.revModel;
import com.supraja_y.popularmovies.POJOS.traModel;

import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieDBAPI {


    @GET("/3/movie/{sort}")
    Call<Movies> loadMovies(@Path("sort") String sort, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/videos")
    Call<Trailers> loadTrailers(@Path("id") String id, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/reviews")
    Call<Reviews> loadReviews(@Path("id") String id, @Query("api_key") String api_key);


    class MovieClient
    {
        private MovieDBAPI movieDBAPI;


        public MovieClient()
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.ROOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            movieDBAPI = retrofit.create(MovieDBAPI.class);
        }

        public MovieDBAPI getMovieDBAPI()
        {
            return movieDBAPI;
        }
    }

    class Movies {
        public List<movModel> results;
    }

    class Reviews {
        public List<revModel> results;
    }

    class Trailers {
        public List<traModel> results;
    }
}
