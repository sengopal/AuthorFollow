package com.capstone.authorfollow.authors;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.capstone.authorfollow.AuthorFollowApplication;
import com.capstone.authorfollow.BaseActivity;
import com.capstone.authorfollow.BookDetailFragment;
import com.capstone.authorfollow.CommonUtil;
import com.capstone.authorfollow.Constants;
import com.capstone.authorfollow.R;
import com.capstone.authorfollow.SimilarBooksAdapter;
import com.capstone.authorfollow.SimilarBooksAdapter.OnBookClickListener;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.NetworkResponse;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.service.BooksDataLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.capstone.authorfollow.Constants.TrackEvents.ADD_AUTHOR;
import static com.capstone.authorfollow.Constants.TrackEvents.REMOVE_AUTHOR;


/**
 * A fragment representing a single Author detail screen.
 * This fragment is either contained in a {@link AuthorListActivity}
 * in two-pane mode (on tablets) or a {@link AuthorDetailActivity}
 * on handsets.
 */
public class AuthorDetailFragment extends Fragment implements OnBookClickListener, LoaderManager.LoaderCallbacks<NetworkResponse<List<UpcomingBook>>> {
    public static final String AUTHOR_DETAIL_FRAGMENT_TAG = "ATFRAGTAG";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
    private static final String TAG = AuthorDetailFragment.class.getSimpleName();

    @Nullable
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.author_detail_poster_image_view)
    CircleImageView authorAvatarImgView;

    @Bind(R.id.author_detail_backdrop_image_view)
    ImageView backdropImgView;

    @Bind(R.id.author_detail_fan_count_text_view)
    TextView fanCountTextView;

    @Bind(R.id.author_detail_bday_text_view)
    TextView bdayTextView;

    @Bind(R.id.author_detail_hometown_text_view)
    TextView homeTownTextView;

    @Bind(R.id.author_detail_about_data_text_view)
    TextView aboutDataTextView;

    @Nullable
    @Bind(R.id.fab)
    FloatingActionButton mFavoriteFab;

    @Bind({R.id.appbar, R.id.inc_author_detail})
    List<View> viewContainers;

    @Bind(R.id.inc_no_selected_movie)
    View noSelectedView;

    @Bind(R.id.recycler_view_similar_movies_list)
    RecyclerView similarBooksListView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.no_upcoming_books_found_text_view)
    TextView noUpcomingBooksTextView;

    @Bind(R.id.gr_icon_image_view)
    ImageView grLinkImgView;

    private AuthorFollow authorFollow;
    private boolean isAddedToFollow;
    private SimilarBooksAdapter similarBooksAdapter;
    private Tracker mTracker;

    public AuthorDetailFragment() {

    }

    public static Fragment newInstance(Bundle bundle) {
        AuthorDetailFragment fragment = new AuthorDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        AuthorFollowApplication application = (AuthorFollowApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        if (null != getArguments() && getArguments().containsKey(Constants.AUTHOR_DETAIL)) {
            authorFollow = getArguments().getParcelable(Constants.AUTHOR_DETAIL);
            Log.d(TAG, "onCreate() called with: " + "authorFollow = [" + authorFollow + "]");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View rootView = inflater.inflate(R.layout.fragment_author_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.setToolbar(mToolbar, true, true);
        }

        setupViewElements();
        initSimilarBooks();
        setupFabButton();
        return rootView;
    }

    private void setupFabButton() {
        if (null != authorFollow && DBHelper.checkInFollowList(authorFollow.getGrAuthorKey())) {
            isAddedToFollow = true;
        }
        syncFabButtonState();

        mFavoriteFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAddedToFollow) {
                    DBHelper.addToFollowList(authorFollow);
                    isAddedToFollow = true;
                    Snackbar.make(getView(), getString(R.string.author_added_to_wishlist), Snackbar.LENGTH_LONG).show();
                    trackAuthorUpdate(true);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getString(R.string.author_remove_confirm_title));
                    builder.setMessage(getString(R.string.author_remove_confirm_msg));
                    builder.setPositiveButton(getString(R.string.confirmation_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHelper.removeFromFollowList(authorFollow);
                            Snackbar.make(getView(), getString(R.string.author_removed_from_wishlist), Snackbar.LENGTH_LONG).show();
                            isAddedToFollow = false;
                            syncFabButtonState();
                            trackAuthorUpdate(false);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.confirmation_cancel), null);
                    builder.show();
                }
                syncFabButtonState();
            }
        });
    }

    private void trackAuthorUpdate(boolean authorAdded) {
        int value = authorAdded ? 1 : -1;
        String event = authorAdded ? ADD_AUTHOR : REMOVE_AUTHOR;
        getTracker().send(new HitBuilders.EventBuilder().setCategory(Constants.TrackScreens.AUTHOR_DETAIL).setAction(event).setLabel(authorFollow.getName()).setValue(value).build());
    }

    protected Tracker getTracker() {
        return mTracker;
    }

    private void syncFabButtonState() {
        mFavoriteFab.setImageDrawable(ContextCompat.getDrawable(getContext(), isAddedToFollow ? R.drawable.ic_close_white_36px : R.drawable.ic_check_white_36px));
    }

    private void initSimilarBooks() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        similarBooksAdapter = new SimilarBooksAdapter(AuthorDetailFragment.this);
        similarBooksListView.setLayoutManager(linearLayoutManager);
        similarBooksListView.setAdapter(similarBooksAdapter);
        similarBooksListView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.spacing_small)));
        if (null != authorFollow) {
            List<UpcomingBook> booksFromDB = DBHelper.getBooksFromAuthor(authorFollow.getName());
            if (null != booksFromDB && !booksFromDB.isEmpty()) {
                similarBooksAdapter.setSimilarMovies(booksFromDB);
            } else {
                //load from Network
                progressBar.setVisibility(View.VISIBLE);
                getLoaderManager().initLoader(0, null, this);
            }
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<NetworkResponse<List<UpcomingBook>>> onCreateLoader(int id, Bundle args) {
        return new BooksDataLoader(getActivity(), Arrays.asList(new String[]{authorFollow.getName()}));
    }

    @Override
    public void onLoadFinished(Loader<NetworkResponse<List<UpcomingBook>>> loader, NetworkResponse<List<UpcomingBook>> response) {
        progressBar.setVisibility(View.GONE);
        if (response.isSuccess()) {
            List<UpcomingBook> bookList = response.getResponse();
            similarBooksAdapter.setSimilarMovies(bookList);
            if (null != bookList && !bookList.isEmpty()) {
                Snackbar.make(getView(), R.string.books_data_loaded, Snackbar.LENGTH_LONG).show();
            } else {
                noUpcomingBooksTextView.setVisibility(View.VISIBLE);
            }
        } else {
            Snackbar.make(getView(), response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
            noUpcomingBooksTextView.setVisibility(View.VISIBLE);
            if (!CommonUtil.isConnected(getActivity())) {

            }
        }
        //If there are no upcoming books for selected Authors
        //TODO: Set no upcoming books message
    }

    @Override
    public void onLoaderReset(Loader<NetworkResponse<List<UpcomingBook>>> loader) {

    }

    private void setupViewElements() {
        if (authorFollow == null) {
            toggleNonSelectedView(true);
            return;
        }
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(authorFollow.getName());
        }

        toggleNonSelectedView(false);

        String imageUrl = authorFollow.getImageUrl();
        Picasso.with(getActivity()).load(imageUrl).into(backdropImgView);
        if (null != authorFollow) {
            Picasso.with(getActivity()).load(authorFollow.getImageUrl()).placeholder(R.drawable.author_placeholder).into(authorAvatarImgView);
        }

        String fanCount = getFormattedFanCount(authorFollow.getFanCount());
        fanCountTextView.setText(fanCount);
        fanCountTextView.setContentDescription(getString(R.string.a11y_movie_rate, authorFollow.getFanCount()));

        bdayTextView.setText(getFormattedBday(authorFollow.getDateOfBirth()));
        homeTownTextView.setText(authorFollow.getHomeTown());

        aboutDataTextView.setText(android.text.Html.fromHtml(authorFollow.getDesc()));
        aboutDataTextView.setContentDescription(getString(R.string.a11y_movie_overview, authorFollow.getDesc()));

        grLinkImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authorFollow.getGrPageLink()));
                startActivity(i);
            }
        });
    }

    private String getFormattedBday(Date dateOfBirth) {
        String bday = getString(R.string.not_available);
        if (null != dateOfBirth) {
            bday = DATE_FORMAT.format(dateOfBirth);
        }
        return bday;
    }

    private String getFormattedFanCount(String fanCountStr) {
        String fanCount = getString(R.string.not_available);
        try {
            int fanCountNum = Integer.parseInt(fanCountStr);
            if (fanCountNum < 1000) {
                fanCount = fanCountStr;
            } else {
                int exp = (int) (Math.log(fanCountNum) / Math.log(1000));
                fanCount = String.format("%.1f %c", fanCountNum / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
            }
        } catch (Exception e) {
            Log.e("NFE_FAN_CNT", e.getMessage());
        }
        return fanCount;
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

        CommonUtil.setupFragAnimation(fragment);
        //to prevent java.lang.IllegalArgumentException: Unique transitionNames are required for all sharedElements
        ViewCompat.setTransitionName(view, "1");
        getFragmentManager().beginTransaction().addSharedElement(view, Constants.BOOK_POSTER_IMAGE_VIEW_KEY).replace(R.id.detail_container, fragment, BookDetailFragment.DETAIL_FRAGMENT_TAG).addToBackStack(null).commit();
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
