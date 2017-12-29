package com.example.android.project2.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.android.project2.R;
import com.example.android.project2.ui.DetailAdapterClickListener;

public class TrailerViewHolder extends RecyclerView.ViewHolder{

    private View view;
    private TextView trailerName;

    public TrailerViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        trailerName = itemView.findViewById(R.id.list_item_trailer_name);
    }

    public void setTrailerName(String name){
        trailerName.setText(name);
    }

    void setOnClickListener(DetailAdapterClickListener onClickListener, int position){
        view.setOnClickListener( v -> onClickListener.onTrailerClick(position));
    }
}
