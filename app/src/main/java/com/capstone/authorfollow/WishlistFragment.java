package com.capstone.authorfollow;

import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.capstone.authorfollow.BookGridAdaptor.BookSelectionListener;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.data.types.WishlistBook;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WishlistFragment extends Fragment {

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Bind(R.id.book_list_recycle_view)
    RecyclerView mPopularGridView;

    @Bind(R.id.main_grid_empty_container)
    LinearLayout mNoMovieContainer;

    @Bind(R.id.main_movie_sw_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<UpcomingBook> bookList;
    private BookGridAdaptor bookGridAdaptor;

    private int selectedPosition;

    private Handler handler;

    private ContentObserver contentObserver;

    public static WishlistFragment newInstance() {
        WishlistFragment fragment = new WishlistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO Mladen stage 2
        //outState.putInt(Constants.POSITION_KEY, bookGridAdaptor.getSelectedPosition());
    }

    private void registerObserver() {
        handler = new Handler();
        contentObserver = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                bookList.clear();
                bookList.addAll(DBHelper.wishlist());
                bookGridAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                onChange(selfChange);
            }
        };
        getContext().getContentResolver().registerContentObserver(WishlistBook.CONTENT_URI, true, contentObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterObserver();
    }

    private void unregisterObserver() {
        getContext().getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver = null;
        handler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);

        int gridColumns = getResources().getInteger(R.integer.grid_columns);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), gridColumns);

        mPopularGridView.setLayoutManager(mLayoutManager);
        mPopularGridView.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        int colorPrimaryLight = ContextCompat.getColor(getActivity(), (R.color.colorPrimaryTransparent));
        mPopularGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        bookList = DBHelper.wishlist();

        bookGridAdaptor = new BookGridAdaptor((BookSelectionListener) getActivity(), bookList, colorPrimaryLight);
        mPopularGridView.setAdapter(bookGridAdaptor);
        mSwipeRefreshLayout.setRefreshing(false);
        registerObserver();
        return view;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String sortType;
        boolean result;

        switch (item.getItemId()) {
            case R.id.sort_by_popularity_desc:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = true;
                break;
            case R.id.sort_by_rates_desc:
                sortType = Constants.SORT_BY_RATING_DESC;
                result = true;
                break;
            default:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = super.onOptionsItemSelected(item);
                break;
        }
        PreferenceUtil.savePrefs(getActivity(), Constants.SORT_BY_KEY, sortType);
        restartLoader();
        return result;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_in_wishlist));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookGridAdaptor.addBooks(DBHelper.filterWishlist(query));
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                bookGridAdaptor.addBooks(DBHelper.wishlist());
                return false;
            }
        });
    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /*
    public void onLoadFinished(Loader<NetworkResponse<List<UpcomingBook>>> loader, NetworkResponse<List<UpcomingBook>> response) {
        bookGridAdaptor.addBooks(bookList);
        Snackbar.make(getView(), R.string.movies_data_loaded, Snackbar.LENGTH_LONG).show();
    }*/

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(space, space, space, space);
        }
    }
}
