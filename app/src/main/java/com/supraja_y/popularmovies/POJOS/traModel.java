package com.supraja_y.popularmovies.POJOS;

import android.os.Parcel;
import android.os.Parcelable;


public class traModel implements Parcelable {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String id;

    public String getKey() {
        return key;
    }

    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(key);
    }

    public traModel() {
    }

    protected traModel(Parcel in) {
        id = in.readString();
        key = in.readString();
    }

    public static final Creator<traModel> CREATOR = new Creator<traModel>() {
        @Override
        public traModel createFromParcel(Parcel in) {
            return new traModel(in);
        }

        @Override
        public traModel[] newArray(int size) {
            return new traModel[size];
        }
    };

}
