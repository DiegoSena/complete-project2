package com.example.android.project2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ReviewViewHolder extends RecyclerView.ViewHolder{
    private TextView author;
    private TextView content;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.list_item_review_author);
        content = itemView.findViewById(R.id.list_item_review_content);
    }

    public void setAuthor(String author){
        this.author.setText(author);
    }

    public void setContent(String content){
        this.content.setText(content);
    }

}
