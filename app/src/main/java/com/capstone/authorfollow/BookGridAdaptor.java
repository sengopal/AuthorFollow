package com.capstone.authorfollow;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.authorfollow.data.types.UpcomingBook;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookGridAdaptor extends RecyclerView.Adapter<BookGridAdaptor.ViewHolder> {
    private final BookSelectionListener callback;
    private List<UpcomingBook> bookList = new ArrayList<>();
    private int mDefaultColor;
    private int mSelectedPosition;

    public interface BookSelectionListener {
        void onItemSelected(UpcomingBook bookData, Bitmap posterBitmap, View view);
    }

    public BookGridAdaptor(BookSelectionListener callback, List<UpcomingBook> bookList, int mDefaultColor) {
        this.callback = callback;
        this.bookList = bookList;
        this.mDefaultColor = mDefaultColor;
    }

    @Override
    public BookGridAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_column, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BookGridAdaptor.ViewHolder holder, int position) {
        final UpcomingBook bookData = bookList.get(position);
        mSelectedPosition = position;
        holder.mGridItemContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bitmap posterBitmap = ((BitmapDrawable) holder.mMovieImageView.getDrawable()).getBitmap();
                callback.onItemSelected(bookData, posterBitmap, holder.mMovieImageView);
            }
        });

        //holder.mGridItemContainer.setContentDescription(holder.mGridItemContainer.getContext().getString(R.string.a11y_movie_title, bookData.originalTitle));

        holder.authorTextView.setText(bookData.getAuthor());
        holder.authorTextView.setContentDescription(holder.authorTextView.getContext().getString(R.string.a11y_book_author, bookData.getAuthor()));
        /*
        if (Constants.SORT_BY_POPULARITY_DESC.equals(sortType)) {
            setIconForType(holder, sortType, bookData.popularity);
            holder.mSortTypeValueTextView.setText(String.valueOf(Math.round(bookData.popularity)));
        } else {
            setIconForType(holder, sortType, bookData.voteAverage);
            holder.mSortTypeValueTextView.setText(String.valueOf(Math.round(bookData.voteAverage)));
        }*/

        final RelativeLayout container = holder.mMovieTitleContainer;
        String imageUrl = (null != bookData.getSmallImageUrl()) ? bookData.getSmallImageUrl() : bookData.getBigImageUrl();
        Picasso.with(holder.mMovieImageView.getContext()).load(imageUrl).placeholder(R.drawable.ic_movie_placeholder).
                into(holder.mMovieImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap posterBitmap = ((BitmapDrawable) holder.mMovieImageView.getDrawable()).getBitmap();
                        Palette.from(posterBitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                container.setBackgroundColor(ColorUtils.setAlphaComponent(palette.getMutedColor(mDefaultColor), 190)); //Opacity
                            }
                        });
                    }

                    @Override
                    public void onError() {
                    }
                });

    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public void addBooks(List<UpcomingBook> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        bookList = data;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int mSelectedPosition) {
        this.mSelectedPosition = mSelectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.grid_item_author_text_view)
        TextView authorTextView;

        @Bind(R.id.grid_item_poster_image_view)
        ImageView mMovieImageView;

        @Bind(R.id.grid_item_sort_type_image_view)
        ImageView mSortTypeIconImageView;

        @Bind(R.id.grid_item_title_container)
        RelativeLayout mMovieTitleContainer;

        @Bind(R.id.grid_item_container)
        FrameLayout mGridItemContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
