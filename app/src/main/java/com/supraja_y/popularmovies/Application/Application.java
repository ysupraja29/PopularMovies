package com.supraja_y.popularmovies.Application;


import com.supraja_y.popularmovies.RetroFit.MovieDBAPI;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class Application extends android.app.Application {
    private static MovieDBAPI.MovieClient movieClient;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        movieClient = new MovieDBAPI.MovieClient();
    }

    public static MovieDBAPI.MovieClient getMovieClient() {
        return movieClient;
    }
}
