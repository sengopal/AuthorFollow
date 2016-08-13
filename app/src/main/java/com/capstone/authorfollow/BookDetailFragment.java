package com.capstone.authorfollow;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.capstone.authorfollow.SimilarBooksAdapter.OnBookClickListener;
import com.capstone.authorfollow.authors.AuthorDetailFragment;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.capstone.authorfollow.Constants.TrackEvents.ADD_BOOK;
import static com.capstone.authorfollow.Constants.TrackEvents.REMOVE_BOOK;


/**
 * A fragment representing a single UpcomingBook detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment implements OnBookClickListener, GenresListAdapter.OnGenreClickListener {
    public static final String DETAIL_FRAGMENT_TAG = "BKFRAGTAG";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
    private static final String TAG = BookDetailFragment.class.getSimpleName();

    @Nullable
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.book_detail_poster_image_view)
    ImageView posterImgView;

    @Bind(R.id.book_detail_backdrop_image_view)
    ImageView backdropImgView;

    @Bind(R.id.pub_date_text_view)
    TextView pubDateTextView;

    @Bind(R.id.rating_value_view)
    RatingBar ratingBar;

    @Bind(R.id.book_detail_isbn_text_view)
    TextView isbnTextView;

    @Bind(R.id.rate_value_text_view)
    TextView mDetailRateTextView;

    @Bind(R.id.desc_text_view)
    TextView descTextView;

    @Nullable
    @Bind(R.id.fab)
    FloatingActionButton mFavoriteFab;

    @Bind({R.id.appbar, R.id.inc_book_detail})
    List<View> viewContainers;

    @Bind(R.id.inc_no_book_selected)
    View noSelectedView;

    @Bind(R.id.recycler_view_similar_books_list)
    RecyclerView similarBooksListView;

    @Bind(R.id.recycler_view_genres_list)
    RecyclerView genresListView;

    @Bind(R.id.author_avatar)
    CircleImageView authorAvatarImgView;

    @Bind(R.id.amazon_icon_image_view)
    ImageView amazonLinkImgView;

    @Bind(R.id.gr_icon_image_view)
    ImageView grLinkImgView;

    private UpcomingBook upcomingBook;
    private boolean isAddedToWishlist;
    private SimilarBooksAdapter similarBooksAdapter;
    private GenresListAdapter genresListAdapter;
    private ShareActionProvider mShareActionProvider;
    private Tracker mTracker;

    public BookDetailFragment() {

    }

    public static Fragment newInstance(Bundle bundle) {
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    protected Tracker getTracker() {
        return mTracker;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        AuthorFollowApplication application = (AuthorFollowApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        if (null != getArguments() && getArguments().containsKey(Constants.BOOK_DETAIL)) {
            upcomingBook = getArguments().getParcelable(Constants.BOOK_DETAIL);
            Log.d(TAG, "onCreate() called with: " + "upcomingBook = [" + upcomingBook + "]");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.setToolbar(mToolbar, true, true);
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
        genresListView.addItemDecoration(new CloseSpacingItemDecoration((int) getResources().getDimension(R.dimen.spacing_small)));
        genresListView.setHasFixedSize(true);
        if (null != upcomingBook) {
            genresListAdapter.setGenres(upcomingBook.getGenres());
        }
    }

    private void setupFabButton() {
        if (null != upcomingBook && DBHelper.checkInWishlist(upcomingBook.getGrApiId())) {
            isAddedToWishlist = true;
        }
        syncFabButtonState();

        mFavoriteFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAddedToWishlist) {
                    DBHelper.addToWishlist(upcomingBook);
                    isAddedToWishlist = true;
                    Snackbar.make(getView(), getString(R.string.book_added_to_wishlist), Snackbar.LENGTH_LONG).show();
                    trackBookUpdate(true);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getString(R.string.wishlist_remove_confirm_title));
                    builder.setMessage(getString(R.string.wishlist_remove_confirm_msg));
                    builder.setPositiveButton(getString(R.string.confirmation_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHelper.removeFromWishlist(upcomingBook.getGrApiId());
                            Snackbar.make(getView(), getString(R.string.book_removed_from_wishlist), Snackbar.LENGTH_LONG).show();
                            isAddedToWishlist = false;
                            trackBookUpdate(false);
                            syncFabButtonState();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.confirmation_cancel), null);
                    builder.show();
                }
                syncFabButtonState();
            }
        });
    }

    private void trackBookUpdate(boolean bookAdded) {
        int value = bookAdded ? 1 : -1;
        String event = bookAdded ? ADD_BOOK : REMOVE_BOOK;
        getTracker().send(new HitBuilders.EventBuilder().setCategory(Constants.TrackScreens.BOOK_DETAIL).setAction(event).setLabel(upcomingBook.getTitle()).setValue(value).build());
    }

    private void syncFabButtonState() {
        mFavoriteFab.setImageDrawable(ContextCompat.getDrawable(getContext(), isAddedToWishlist ? R.drawable.ic_close_white_36px : R.drawable.ic_check_white_36px));
    }

    private void initSimilarBooks() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        similarBooksAdapter = new SimilarBooksAdapter(BookDetailFragment.this);
        similarBooksListView.setLayoutManager(linearLayoutManager);
        similarBooksListView.setAdapter(similarBooksAdapter);
        similarBooksListView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.spacing_small)));
        if (null != upcomingBook) {
            similarBooksAdapter.setSimilarBooks(DBHelper.getBooksFromAuthor(upcomingBook.getAuthor()));
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
        Picasso.with(getActivity()).load(imageUrl).into(backdropImgView);
        Picasso.with(getActivity()).load(upcomingBook.getSmallImageUrl()).placeholder(R.drawable.book_placeholder).into(posterImgView);
        final AuthorFollow authorInfo = DBHelper.getAuthorInfo(upcomingBook.getAuthor());
        if (null != authorInfo) {
            Picasso.with(getActivity()).load(authorInfo.getImageUrl()).placeholder(R.drawable.author_placeholder).into(authorAvatarImgView);
            authorAvatarImgView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putParcelable(Constants.AUTHOR_DETAIL, authorInfo);
                    AuthorDetailFragment fragment = new AuthorDetailFragment();
                    fragment.setArguments(args);
                    CommonUtil.setupFragAnimation(fragment);
                    //to prevent java.lang.IllegalArgumentException: Unique transitionNames are required for all sharedElements
                    ViewCompat.setTransitionName(view, "3");
                    getFragmentManager().beginTransaction().addSharedElement(view, Constants.AUTHOR_POSTER_IMAGE_VIEW_KEY).replace(R.id.detail_container, fragment, AuthorDetailFragment.AUTHOR_DETAIL_FRAGMENT_TAG).addToBackStack(null).commit();
                }
            });
        }

        String rating = upcomingBook.getRating() > 0 ? String.format("%2.1f", upcomingBook.getRating()) : getString(R.string.not_rated);

        ratingBar.setRating(upcomingBook.getRating());

        mDetailRateTextView.setText(rating);
        mDetailRateTextView.setContentDescription(getString(R.string.a11y_book_rate, rating));


        isbnTextView.setText(upcomingBook.getIsbn());

        if (null != upcomingBook.getDescription()) {
            descTextView.setText(android.text.Html.fromHtml(upcomingBook.getDescription()));
            descTextView.setContentDescription(getString(R.string.a11y_book_overview, upcomingBook.getDescription()));
        } else {
            descTextView.setText(getString(R.string.no_description_available));
            descTextView.setContentDescription(getString(R.string.no_description_available));
        }

        if (null != upcomingBook.getPublishedDate()) {
            pubDateTextView.setText(DATE_FORMAT.format(upcomingBook.getPublishedDate()));
            pubDateTextView.setContentDescription(getString(R.string.a11y_book_year, upcomingBook.getPublishedDate()));
        }

        amazonLinkImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(upcomingBook.getAmazonLink()));
                startActivity(i);
            }
        });

        grLinkImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(upcomingBook.getGrLink()));
                startActivity(i);
            }
        });
    }

    private void toggleNonSelectedView(boolean noBookData) {
        toggleVisibleFab(!noBookData);
        noSelectedView.setVisibility(noBookData ? View.VISIBLE : View.GONE);
        for (View view : viewContainers) {
            view.setVisibility(noBookData ? View.GONE : View.VISIBLE);
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
        ViewCompat.setTransitionName(view, "2");

        getFragmentManager().beginTransaction().addSharedElement(view, Constants.BOOK_POSTER_IMAGE_VIEW_KEY).replace(R.id.detail_container, fragment, BookDetailFragment.DETAIL_FRAGMENT_TAG).addToBackStack(null).commit();
    }

    @Override
    public void onGenreClick(String genre) {
        Intent intent = new Intent(getContext(), BookListActivity.class);
        intent.putExtra(Constants.GENRE_SEARCH, genre);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
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

    private class CloseSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spacing;

        public CloseSpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                return;
            }
            outRect.left = 0;
            outRect.right = 0;
        }
    }

    //Reference: https://gist.github.com/mariol3/d43b0629756b8227e037
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bookdetail_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (mShareActionProvider != null) {
            Intent mShareIntent = new Intent(Intent.ACTION_SEND);
            mShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            mShareIntent.setType("text/plain");
            mShareIntent.putExtra(Intent.EXTRA_TEXT, upcomingBook.getAmazonLink());
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }
}
