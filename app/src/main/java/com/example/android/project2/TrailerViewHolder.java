package com.example.android.project2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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

    void setOnClickListener(AdapterClickListener onClickListener, int position){
        view.setOnClickListener( v -> onClickListener.onTrailerClick(position));
    }
}
