package com.supraja_y.popularmovies.POJOS;


import android.os.Parcel;
import android.os.Parcelable;

public class revModel implements Parcelable {


    public String getId() {
        return id;
    }


    private String id;

    public String getcontent() {
        return content;
    }


    private String content;

    public String getAuthor() {
        return author;
    }


    private String author;

    public String getUrl() {
        return url;
    }

    private String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);


    }

    protected revModel(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<revModel> CREATOR = new Creator<revModel>() {
        @Override
        public revModel createFromParcel(Parcel in) {
            return new revModel(in);
        }

        @Override
        public revModel[] newArray(int size) {
            return new revModel[size];
        }
    };
}