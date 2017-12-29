package com.example.android.project2.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.project2.R;
import com.example.android.project2.model.Movie;
import com.example.android.project2.ui.MovieAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    public static final String IMAGE_BASE_URL_SIZE_W342 = "http://image.tmdb.org/t/p/w342";
    private final Context context;
    private final MovieAdapterItemClickListener movieAdapterClickListener;
    private List<Movie> movies = new ArrayList<>();

    public MovieAdapter(Context context, MovieAdapterItemClickListener movieAdapterItemClickListener) {
        this.context = context;
        this.movieAdapterClickListener = movieAdapterItemClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
        return new MovieViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        holder.setItemClickListener(this.movieAdapterClickListener, position);
        holder.setPoster(IMAGE_BASE_URL_SIZE_W342 + "/" + movie.getPosterPath(), context);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addAll(List<Movie> movies) {
        this.movies.addAll(movies);
    }

    public void clear() {
        this.movies.clear();
    }
}
