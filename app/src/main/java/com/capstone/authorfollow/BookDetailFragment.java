package com.capstone.authorfollow;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.capstone.authorfollow.GenresListAdapter.OnGenreClickListener;
import com.capstone.authorfollow.SimilarBooksAdapter.OnBookClickListener;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single UpcomingBook detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment implements OnBookClickListener, OnGenreClickListener {
    public static final String DETAIL_FRAGMENT_TAG = "BKFRAGTAG";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
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

    @Bind(R.id.movie_detail_rating_value_view)
    RatingBar ratingBar;


    @Bind(R.id.book_detail_isbn_text_view)
    TextView isbnTextView;

//    @Bind(R.id.playTrailerImageView)
//    ImageView mPlayTrailerImageView;

    @Bind(R.id.movie_detail_rate_value_text_view)
    TextView mDetailRateTextView;

    @Bind(R.id.movie_detail_synopsys_data_text_view)
    TextView mDetailMovieSynopsis;

    @Nullable
    @Bind(R.id.fab)
    FloatingActionButton mFavoriteFab;

    @Bind({R.id.appbar, R.id.inc_movie_detail})
    List<View> viewContainers;

    @Bind(R.id.inc_no_selected_movie)
    View noSelectedView;

    @Bind(R.id.recycler_view_similar_movies_list)
    RecyclerView similarBooksListView;

    @Bind(R.id.recycler_view_genres_list)
    RecyclerView genresListView;

    private Bitmap mPosterImage;
    private boolean mTwoPane;
    private UpcomingBook upcomingBook;
    private boolean isAddedToWishlist;
    private SimilarBooksAdapter similarBooksAdapter;
    private GenresListAdapter genresListAdapter;

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
        setHasOptionsMenu(true);
        if (null != getArguments() && getArguments().containsKey(Constants.BOOK_DETAIL)) {
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
        initGenresList();
        initSimilarBooks();
        setupFabButton();

        return rootView;
    }

    private void initGenresList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        genresListAdapter = new GenresListAdapter(BookDetailFragment.this);
        genresListView.setLayoutManager(linearLayoutManager);
        genresListView.setAdapter(genresListAdapter);
        genresListView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.spacing_small)));
        genresListView.setHasFixedSize(true);
        if (null != upcomingBook) {
            genresListAdapter.setGenres(upcomingBook.getGenres());
        }
    }

    private void setupFabButton() {
        if (null != upcomingBook && DBHelper.checkInWishlist(upcomingBook.getGrApiId())) {
            isAddedToWishlist = true;
            mFavoriteFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_white_36px));
        }
        mFavoriteFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAddedToWishlist) {
                    DBHelper.addToWishlist(upcomingBook);
                    mFavoriteFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_white_36px));
                    isAddedToWishlist = true;
                    Snackbar.make(getView(), getString(R.string.book_added_to_wishlist), Snackbar.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getString(R.string.wishlist_remove_confirm_title));
                    builder.setMessage(getString(R.string.wishlist_remove_confirm_msg));
                    builder.setPositiveButton(getString(R.string.confirmation_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHelper.removeFromWishlist(upcomingBook.getGrApiId());
                            Snackbar.make(getView(), getString(R.string.book_removed_from_wishlist), Snackbar.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.confirmation_cancel), null);
                    builder.show();
                }
            }
        });
    }

    private void initSimilarBooks() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        similarBooksAdapter = new SimilarBooksAdapter(BookDetailFragment.this);
        similarBooksListView.setLayoutManager(linearLayoutManager);
        similarBooksListView.setAdapter(similarBooksAdapter);
        similarBooksListView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.spacing_small)));
        if (null != upcomingBook) {
            similarBooksAdapter.setSimilarMovies(DBHelper.getBooksFromAuthor(upcomingBook.getAuthor()));
        }
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

        String rating = upcomingBook.getRating() > 0 ? String.format("%2.1f", upcomingBook.getRating()) : getString(R.string.not_rated);

        ratingBar.setRating(upcomingBook.getRating());

        //mDetailMovieTitle.setText(upcomingBook.getTitle());
        //mDetailMovieTitle.setContentDescription(getString(R.string.a11y_movie_title, upcomingBook.getTitle()));

        mDetailRateTextView.setText(rating);
        mDetailRateTextView.setContentDescription(getString(R.string.a11y_movie_rate, rating));


        isbnTextView.setText(upcomingBook.getIsbn());

        //TODO: Build the description
        mDetailMovieSynopsis.setText(android.text.Html.fromHtml(upcomingBook.getDescription()));
        mDetailMovieSynopsis.setContentDescription(getString(R.string.a11y_movie_overview, upcomingBook.getDescription()));

        if (null != upcomingBook.getPublishedDate()) {
            mDetailMovieYear.setText(DATE_FORMAT.format(upcomingBook.getPublishedDate()));
            mDetailMovieYear.setContentDescription(getString(R.string.a11y_book_year, upcomingBook.getPublishedDate()));
        }
    }

    private void toggleNonSelectedView(boolean noMovieData) {
        toggleVisibleFab(!noMovieData);
        noSelectedView.setVisibility(noMovieData ? View.VISIBLE : View.GONE);
        for (View view : viewContainers) {
            view.setVisibility(noMovieData ? View.GONE : View.VISIBLE);
        }
    }

    private void toggleVisibleFab(boolean showFab) {
        if (mFavoriteFab != null) {
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

    @Override
    public void onClick(UpcomingBook bookData, Bitmap posterBitmap, View view) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.BOOK_DETAIL, bookData);
        args.putParcelable(Constants.POSTER_IMAGE_KEY, posterBitmap);
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.book_detail_container, fragment, BookDetailFragment.DETAIL_FRAGMENT_TAG).addToBackStack(null).commit();
    }

    @Override
    public void onGenreClick(String genre) {

    }

    private class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spacing;

        public SpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                return;
            }
            outRect.left = spacing;
        }
    }
}
