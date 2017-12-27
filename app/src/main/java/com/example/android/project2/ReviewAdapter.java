package com.example.android.project2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_reviews, parent, false);
        return new ReviewViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review review = reviews.get(position);
        holder.setAuthor(review.getAuthor());
        holder.setContent(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
