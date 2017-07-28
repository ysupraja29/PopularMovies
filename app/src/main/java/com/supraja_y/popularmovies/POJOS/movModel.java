package com.supraja_y.popularmovies.POJOS;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class movModel extends RealmObject implements Parcelable{

    private String release_date;
    private String original_title;
    private String poster_path;
    private String overview;
    private float vote_average;
    private String backdrop_path;
    private String id;


    public movModel(){

    }

    public movModel(movModel movModel){
        release_date = movModel.release_date;
        original_title = movModel.original_title;
        poster_path = movModel.poster_path;
        overview = movModel.overview;
        vote_average = movModel.vote_average;
        backdrop_path = movModel.backdrop_path;
        id = movModel.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeFloat(vote_average);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(id);

    }

    protected movModel(Parcel in) {
        original_title = in.readString();
        poster_path = in.readString();
        vote_average = in.readFloat();
        backdrop_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readString();
    }

    public static final Creator<movModel> CREATOR = new Creator<movModel>() {
        @Override
        public movModel createFromParcel(Parcel in) {
            return new movModel(in);
        }

        @Override
        public movModel[] newArray(int size) {
            return new movModel[size];
        }
    };

    public String getoriginal_title() {
        return original_title;
    }



    public String getposter_path() {
        return poster_path;
    }




    public String getOverview() {
        return overview;
    }



    public float getvote_average() {
        return vote_average;
    }



    public String getrelease_date() {
        return release_date;
    }




    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getId() {
        return id;
    }




}
