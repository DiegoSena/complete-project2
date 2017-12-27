package com.example.android.project2.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    public Trailer(String key, String id, String name){
        this.key = key;
        this.id = id;
        this.name = name;
    }

    private String key;
    private String id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getKey());
        dest.writeString(getId());
        dest.writeString(getName());
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel pc) {
            return new Trailer(pc);
        }
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public Trailer(Parcel pc){
        setKey(pc.readString());
        setId(pc.readString());
        setName(pc.readString());
    }
}
