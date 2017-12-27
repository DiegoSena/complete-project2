package com.example.android.project2.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.project2.R;
import com.example.android.project2.model.Trailer;
import com.example.android.project2.ui.AdapterClickListener;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerViewHolder>{

    private final AdapterClickListener onClickListener;
    private List<Trailer> trailers;

    public TrailerAdapter(List<Trailer> trailers, AdapterClickListener onClickListener) {
        this.trailers = trailers;
        this.onClickListener = onClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_trailers, parent, false);
        return new TrailerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        final Trailer trailer = trailers.get(position);
        holder.setTrailerName(trailer.getName());
        holder.setOnClickListener(onClickListener, position);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public Trailer getTrailerFromPosition(int position){
        return trailers.get(position);
    }
}
