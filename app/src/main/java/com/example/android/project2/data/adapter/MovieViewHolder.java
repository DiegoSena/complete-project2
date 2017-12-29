package com.example.android.project2.data.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.android.project2.R;
import com.example.android.project2.ui.MovieAdapterItemClickListener;
import com.squareup.picasso.Picasso;

/**
 * Created by diego on 28/12/17.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder{
    private final View view;
    private ImageView poster;

    public MovieViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        this.poster = itemView.findViewById(R.id.list_item_movie_imageview);
    }

    public void setPoster(String posterPath, Context context) {
        Picasso.with(context).
                load(posterPath).
                placeholder(R.drawable.uploading)
                .error(R.drawable.error_loading).
                into(poster);
    }

    public void setItemClickListener(MovieAdapterItemClickListener movieAdapterClickListener, int position) {
        view.setOnClickListener(v -> movieAdapterClickListener.onMovieClick(position));
    }
}
