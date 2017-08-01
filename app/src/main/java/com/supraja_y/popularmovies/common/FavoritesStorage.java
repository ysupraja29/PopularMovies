package com.supraja_y.popularmovies.common;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.supraja_y.popularmovies.POJOS.movModel;
import com.supraja_y.popularmovies.POJOS.revModel;
import com.supraja_y.popularmovies.POJOS.traModel;

import java.util.ArrayList;
import java.util.List;


public class FavoritesStorage {
    public static void addFavorite(Context context, movModel movie,
                                   ArrayList<traModel> trailerList, ArrayList<revModel> reviewList) {
        final ContentResolver contentResolver = context.getContentResolver();

        contentResolver.delete(movModel.Contract.CONTENT_URI,
                movModel.Contract.MOVIE_ID + "= ?", new String[]{movie.getId()});

        ContentValues movieValues = new ContentValues();
        movieValues.put(movModel.Contract.MOVIE_ID, movie.getId());
        movieValues.put(movModel.Contract.BACKDROP_PATH, movie.getBackdrop_path());
        movieValues.put(movModel.Contract.ORIGINAL_TITLE, movie.getoriginal_title());
        movieValues.put(movModel.Contract.OVERVIEW, movie.getOverview());
        movieValues.put(movModel.Contract.POSTER_PATH, movie.getposter_path());
        movieValues.put(movModel.Contract.RELEASE_DATE, movie.getrelease_date());
        movieValues.put(movModel.Contract.VOTE_AVERAGE, movie.getvote_average());
        contentResolver.insert(movModel.Contract.CONTENT_URI, movieValues);

        saveTrailersOfFavoritemovModel(context, movie.getId(), trailerList);
        saveReviewsOfFavoritemovModel(context, movie.getId(), reviewList);
    }

    public static void saveReviewsOfFavoritemovModel(Context context, String movieId, List<revModel> reviewList) {
        final ContentResolver contentResolver = context.getContentResolver();

        contentResolver.delete(revModel.Contract.CONTENT_URI,
                revModel.Contract.MOVIE_ID + "= ?", new String[]{movieId});

        if (reviewList != null && reviewList.size() != 0) {
            ArrayList<ContentValues> reviewListValues = new ArrayList<>();
            for (revModel review : reviewList) {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(revModel.Contract.MOVIE_ID, movieId);
                reviewValues.put(revModel.Contract.AUTHOR, review.getAuthor());
                reviewValues.put(revModel.Contract.CONTENT, review.getcontent());
                reviewListValues.add(reviewValues);
            }
            if (reviewListValues.size() != 0) {
                ContentValues[] reviewListArray = new ContentValues[reviewListValues.size()];
                reviewListValues.toArray(reviewListArray);
                contentResolver.bulkInsert(revModel.Contract.CONTENT_URI, reviewListArray);
            }
        }
    }

    public static void saveTrailersOfFavoritemovModel(Context context, String movieId, List<traModel> trailerList) {
        final ContentResolver contentResolver = context.getContentResolver();

        contentResolver.delete(traModel.Contract.CONTENT_URI,
                traModel.Contract.MOVIE_ID + "= ?", new String[]{movieId});

        if (trailerList != null && trailerList.size() != 0) {
            ArrayList<ContentValues> trailerListValues = new ArrayList<>();
            for (traModel trailer : trailerList) {
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(traModel.Contract.MOVIE_ID, movieId);
                trailerValues.put(traModel.Contract.KEY, trailer.getKey());
                trailerValues.put(traModel.Contract.NAME, trailer.getName());
                trailerListValues.add(trailerValues);
            }
            if (trailerListValues.size() != 0) {
                ContentValues[] trailerListArray = new ContentValues[trailerListValues.size()];
                trailerListValues.toArray(trailerListArray);
                contentResolver.bulkInsert(traModel.Contract.CONTENT_URI, trailerListArray);
            }
        }
    }

    public static void removeFavorite(Context context, String movieId) {
        final ContentResolver contentResolver = context.getContentResolver();

        contentResolver.delete(movModel.Contract.CONTENT_URI,
                movModel.Contract.MOVIE_ID + "= ?", new String[]{movieId});

        contentResolver.delete(traModel.Contract.CONTENT_URI,
                traModel.Contract.MOVIE_ID + "= ?", new String[]{movieId});

        contentResolver.delete(revModel.Contract.CONTENT_URI,
                revModel.Contract.MOVIE_ID + "= ?", new String[]{movieId});
    }

    public static boolean isFavorite(Context context, String movieId) {
        final ContentResolver contentResolver = context.getContentResolver();

        final Cursor cursor = contentResolver.query(movModel.Contract.CONTENT_URI, new String[]{movModel.Contract._ID}, movModel.Contract.MOVIE_ID + "= ?", new String[]{movieId}, "");
        if (cursor == null) {
            return false;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    private static final String[] TRAILER_PROJECTION = new String[]{
            traModel.Contract.KEY,
            traModel.Contract.NAME,
    };

    // these indices must match the projection TRAILER_PROJECTION that don't use getColumnIndex() method
    private static final int INDEX_TRAILER_KEY = 0;
    private static final int INDEX_TRAILER_NAME = 1;

    public static ArrayList<traModel> getTrailerOfFavoritemovModel(Context context, String movieId) {
        final ContentResolver contentResolver = context.getContentResolver();

        ArrayList<traModel> trailerList = new ArrayList<>();

        final Cursor cursor = contentResolver.query(traModel.Contract.CONTENT_URI, TRAILER_PROJECTION, traModel.Contract.MOVIE_ID + "= ?", new String[]{movieId}, "");

        if (cursor == null) {
            return trailerList;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return trailerList;
        }

        traModel trailer;
        while (cursor.moveToNext()) {
            trailer = new traModel();
            trailer.setKey(cursor.getString(INDEX_TRAILER_KEY));
            trailer.setName(cursor.getString(INDEX_TRAILER_NAME));
            trailerList.add(trailer);
        }
        cursor.close();

        return trailerList;
    }

    private static final String[] REVIEW_PROJECTION = new String[]{
            revModel.Contract.AUTHOR,
            revModel.Contract.CONTENT,
    };

    // these indices must match the projection REVIEW_PROJECTION that don't use getColumnIndex() method
    private static final int INDEX_REVIEW_AUTHOR = 0;
    private static final int INDEX_REVIEW_CONTENT = 1;

    public static ArrayList<revModel> getReviewOfFavoritemovModel(Context context, String movieId) {
        final ContentResolver contentResolver = context.getContentResolver();

        ArrayList<revModel> reviewList = new ArrayList<>();

        final Cursor cursor = contentResolver.query(revModel.Contract.CONTENT_URI, REVIEW_PROJECTION, revModel.Contract.MOVIE_ID + "= ?", new String[]{movieId}, "");

        if (cursor == null) {
            return reviewList;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return reviewList;
        }

        revModel review;
        while (cursor.moveToNext()) {
            review = new revModel();
            review.setAuthor(cursor.getString(INDEX_REVIEW_AUTHOR));
            review.setContent(cursor.getString(INDEX_REVIEW_CONTENT));
            reviewList.add(review);
        }
        cursor.close();

        return reviewList;
    }

    private static final String[] MOVIE_PROJECTION = new String[]{
            movModel.Contract.MOVIE_ID,
            movModel.Contract.BACKDROP_PATH,
            movModel.Contract.ORIGINAL_TITLE,
            movModel.Contract.OVERVIEW,
            movModel.Contract.POSTER_PATH,
            movModel.Contract.RELEASE_DATE,
            movModel.Contract.VOTE_AVERAGE
    };

    // these indices must match the projection MOVIE_PROJECTION that don't use getColumnIndex() method
    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_BACKDROP_PATH = 1;
    private static final int INDEX_ORIGINAL_TITLE = 2;
    private static final int INDEX_OVERVIEW = 3;
    private static final int INDEX_POSTER_PATH = 4;
    private static final int INDEX_RELEASE_DATE = 5;
    private static final int INDEX_VOTE_AVERAGE = 6;

    public static ArrayList<movModel> getFavorites(Context context) {
        final ContentResolver contentResolver = context.getContentResolver();

        ArrayList<movModel> movieList = new ArrayList<>();

        final Cursor cursor = contentResolver.query(movModel.Contract.CONTENT_URI, MOVIE_PROJECTION, null, null, "");

        if (cursor == null) {
            return movieList;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return movieList;
        }

        movModel movie;
        while (cursor.moveToNext()) {
            movie = new movModel();
            movie.setId(cursor.getString(INDEX_MOVIE_ID));
            movie.setBackdrop_path(cursor.getString(INDEX_BACKDROP_PATH));
            movie.setOriginal_title(cursor.getString(INDEX_ORIGINAL_TITLE));
            movie.setOverview(cursor.getString(INDEX_OVERVIEW));
            movie.setPoster_path(cursor.getString(INDEX_POSTER_PATH));
            movie.setRelease_date(cursor.getString(INDEX_RELEASE_DATE));
            movie.setVote_average(Float.parseFloat(cursor.getString(INDEX_VOTE_AVERAGE)));
            movieList.add(movie);
        }
        cursor.close();

        return movieList;
    }
}
