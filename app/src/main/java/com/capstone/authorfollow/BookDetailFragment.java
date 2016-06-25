package com.capstone.authorfollow;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.authorfollow.data.types.UpcomingBook;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single UpcomingBook detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {
    public static final String DETAIL_FRAGMENT_TAG = "BKFRAGTAG";

    private static final String TAG = "MovieDetailFragment";

    @Nullable
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.movie_detail_poster_image_view)
    ImageView mPosterMovie;

    @Bind(R.id.movie_detail_backdrop_image_view)
    ImageView mBackdropMovie;

    //@Bind(R.id.movie_detail_title_text_view)
    //TextView mDetailMovieTitle;

    @Bind(R.id.movie_detail_year_text_view)
    TextView mDetailMovieYear;

    @Bind(R.id.movie_detail_rate_image_view)
    ImageView mDetailRateImageView;

//    @Bind(R.id.playTrailerImageView)
//    ImageView mPlayTrailerImageView;

    @Bind(R.id.movie_detail_rate_value_text_view)
    TextView mDetailRateTextView;

    @Bind(R.id.movie_detail_synopsys_data_text_view)
    TextView mDetailMovieSynopsis;

    @Nullable @Bind(R.id.fab)
    FloatingActionButton mFavoriteFab;

    @Bind({R.id.appbar, R.id.inc_movie_detail})
    List<View> viewContainers;

    @Bind(R.id.inc_no_selected_movie)
    View noSelectedView;

    private Bitmap mPosterImage;
    private boolean mTwoPane;
    private UpcomingBook upcomingBook;

    public BookDetailFragment() {
    }

    public static Fragment newInstance(Bundle bundle) {
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (null!=getArguments() && getArguments().containsKey(Constants.BOOK_DETAIL)) {
            //TODO: use a Loader to load content from a content provider.
            upcomingBook = getArguments().getParcelable(Constants.BOOK_DETAIL);
            mPosterImage = getArguments().getParcelable(Constants.POSTER_IMAGE_KEY);
            mTwoPane = getArguments().getBoolean(Constants.TWO_PANE_KEY);

            Log.d(TAG, "onCreate() called with: " + "mMovieData = [" + upcomingBook + "]");
            Log.d(TAG, "onCreate() called with: " + "mPosterImage = [" + mPosterImage + "]");
            Log.d(TAG, "onCreate() called with: " + "mTwoPane = [" + mTwoPane + "]");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity() instanceof BookDetailActivity) {
            BookDetailActivity detailActivity = (BookDetailActivity) getActivity();
            detailActivity.setToolbar(mToolbar, true, true);
        }
        setupViewElements();

        return rootView;
    }

    private void setupViewElements() {
        if (upcomingBook == null) {
            toggleNonSelectedView(true);
            return;
        }
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(upcomingBook.getTitle());
        }

        toggleNonSelectedView(false);

        String imageUrl = upcomingBook.getBigImageUrl();
        Picasso.with(getActivity()).load(imageUrl).into(mBackdropMovie);
        Picasso.with(getActivity()).load(upcomingBook.getSmallImageUrl()).placeholder(R.drawable.ic_movie_placeholder).into(mPosterMovie);

        mDetailRateImageView.setImageResource(CommonUtil.getRateIcon(upcomingBook.getRating(), true));
        mDetailRateImageView.setContentDescription(getString(R.string.a11y_movie_title, upcomingBook.getTitle()));

        //mDetailMovieTitle.setText(upcomingBook.getTitle());
        //mDetailMovieTitle.setContentDescription(getString(R.string.a11y_movie_title, upcomingBook.getTitle()));

        mDetailRateTextView.setText(String.format("%d/10", Math.round(upcomingBook.getRating())));
        mDetailRateTextView.setContentDescription(getString(R.string.a11y_movie_rate, String.format("%d/10", Math.round(upcomingBook.getRating()))));

        //TODO: Build the description
        mDetailMovieSynopsis.setText(upcomingBook.getTitle());
        mDetailMovieSynopsis.setContentDescription(getString(R.string.a11y_movie_overview, upcomingBook.getTitle()));

        mDetailMovieYear.setText(upcomingBook.getPublishedDate());
        mDetailMovieYear.setContentDescription(getString(R.string.a11y_movie_year, upcomingBook.getPublishedDate()));
    }

    private void toggleNonSelectedView(boolean noMovieData) {
        toggleVisibleFab(!noMovieData);
        noSelectedView.setVisibility(noMovieData ? View.VISIBLE : View.GONE);
        for(View view : viewContainers){
            view.setVisibility(noMovieData ? View.GONE : View.VISIBLE);
        }
    }

    private void toggleVisibleFab(boolean showFab) {
        if(mFavoriteFab != null) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mFavoriteFab.getLayoutParams();
            if (showFab) {
                p.setAnchorId(R.id.appbar);
            } else {
                p.setAnchorId(View.NO_ID);
            }
            mFavoriteFab.setLayoutParams(p);
            mFavoriteFab.setVisibility(showFab ? View.VISIBLE : View.GONE);
        }
    }
}
