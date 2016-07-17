package com.capstone.authorfollow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.capstone.authorfollow.CommonUtil.isEmpty;

public class GenresListAdapter extends RecyclerView.Adapter<GenresListAdapter.ViewHolder> {
    private List<String> genres;
    private OnGenreClickListener listener;

    public GenresListAdapter(OnGenreClickListener listener) {
        this.listener = listener;
        this.genres = new ArrayList<>();
    }

    public void setGenres(String csGenres) {
        if(!isEmpty(csGenres)) {
            this.genres = Arrays.asList(csGenres.split(","));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_genre, parent, false);
        final ViewHolder viewHolder = new ViewHolder(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGenreClick(genres.get(viewHolder.getAdapterPosition()));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(genres.get(position));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }

    public interface OnGenreClickListener {
        void onGenreClick(String genre);
    }
}
