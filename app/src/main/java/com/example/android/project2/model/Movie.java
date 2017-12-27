package com.example.android.project2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String posterPath;
    private String title;
    private int releaseYear;
    private double voteAverage;
    private String overview;
    private String id;
    private boolean isFavorite;

    public static final String PARCELABLE_KEY = "movie";

    @Override
    public String toString() {
        return getPosterPath() + getTitle() + getReleaseYear() + getVoteAverage() + getOverview();
    }

    public Movie(String poster_path, String title, int releaseYear, double voteAverage, String overview, String id){
        this.setPosterPath(poster_path);
        this.setTitle(title);
        this.setReleaseYear(releaseYear);
        this.setVoteAverage(voteAverage);
        this.setOverview(overview);
        this.setId(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPosterPath());
        dest.writeString(getTitle());
        dest.writeInt(getReleaseYear());
        dest.writeDouble(getVoteAverage());
        dest.writeString(getOverview());
        dest.writeString(getId());
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(Parcel pc){
        setPosterPath(pc.readString());
        setTitle(pc.readString());
        setReleaseYear(pc.readInt());
        setVoteAverage(pc.readDouble());
        setOverview(pc.readString());
        setId(pc.readString());
        setFavorite(pc.readByte() != 0);
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}
