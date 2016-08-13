package com.capstone.authorfollow;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.authorfollow.data.types.UpcomingBook;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SimilarBooksAdapter extends RecyclerView.Adapter<SimilarBooksAdapter.ViewHolder> {
    private List<UpcomingBook> books;
    private OnBookClickListener listener;

    public SimilarBooksAdapter(OnBookClickListener listener) {
        this.listener = listener;
        this.books = new ArrayList<>();
    }

    public void setSimilarMovies(List<UpcomingBook> similarMovies) {
        this.books = similarMovies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_thumbnail, parent, false);
        final ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.with(holder.imageView.getContext()).load(books.get(position).getSmallImageUrl()).placeholder(R.drawable.book_placeholder).into(holder.imageView);
        holder.textView.setText(books.get(position).getTitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Bitmap posterBitmap = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();
                    listener.onClick(books.get(holder.getAdapterPosition()), posterBitmap, holder.imageView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            this.imageView = (ImageView) cardView.findViewById(R.id.image_view_thumbnail);
            this.textView = (TextView) cardView.findViewById(R.id.text_view_name);
        }
    }

    public interface OnBookClickListener {
        void onClick(UpcomingBook bookData, Bitmap posterBitmap, View view);
    }
}