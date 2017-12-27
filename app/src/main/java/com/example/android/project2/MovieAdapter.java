package com.example.android.project2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, int resource) {
        super(context, 0, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Movie movie = getItem(position);
        if(movie != null){
            String imageSrc = "http://image.tmdb.org/t/p/w342"+ movie.getPosterPath();
            Picasso.with(getContext()).load(imageSrc).into(viewHolder.imageView);
        }
        return convertView;
    }

    static class ViewHolderItem {
        ImageView imageView;
    }
}
