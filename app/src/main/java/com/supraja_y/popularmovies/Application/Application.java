package com.supraja_y.popularmovies.Application;


import com.supraja_y.popularmovies.RetroFit.MovieDBAPI;




public class Application extends android.app.Application {
    private static MovieDBAPI.MovieClient movieClient;

    @Override
    public void onCreate() {
        super.onCreate();
        movieClient = new MovieDBAPI.MovieClient();
    }

    public static MovieDBAPI.MovieClient getMovieClient() {
        return movieClient;
    }
}
