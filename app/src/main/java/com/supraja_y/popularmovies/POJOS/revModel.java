package com.supraja_y.popularmovies.POJOS;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.supraja_y.popularmovies.providers.MovieDbProvider;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

public class revModel implements Parcelable {


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setUrl(String url) {
        this.url = url;
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
    public revModel() {
    }
    public revModel(Parcel in) {
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
    /**
     * Contract for DB and content provider
     */
    public interface Contract extends ProviGenBaseContract {
        @Column(Column.Type.TEXT)
        String MOVIE_ID = "movie_id";

        @Column(Column.Type.TEXT)
        String AUTHOR = "author";

        @Column(Column.Type.TEXT)
        String CONTENT = "content";

        @ContentUri
        Uri CONTENT_URI = MovieDbProvider.getContentUri("reviews");
    }
}