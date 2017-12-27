package com.example.android.project2;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private String id;
    private String author;
    private String content;

    public Review(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getAuthor());
        dest.writeString(getContent());
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel pc) {
            return new Review(pc);
        }
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review(Parcel pc){
        setId(pc.readString());
        setAuthor(pc.readString());
        setContent(pc.readString());
    }

}
